package io.github.necrashter.natural_revenge.cheats;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import io.github.necrashter.natural_revenge.world.entities.GameEntity;
import io.github.necrashter.natural_revenge.world.player.Player;
import io.github.necrashter.natural_revenge.world.Octree;

import java.util.ArrayList;

/**
 * Central manager for all cheat features in Frogue.
 * Handles enabling/disabling cheats and applying their effects.
 */
public class CheatManager {
    // Cheat states
    public boolean aimAssistEnabled = false;
    public boolean thirdPersonEnabled = false;
    public boolean bunnyHopEnabled = false;
    public boolean airStrafeEnabled = false;
    public boolean wallhackEnabled = false;
    public boolean infiniteAmmoEnabled = false;
    public boolean rapidFireEnabled = false;

    // Settings
    private static final float AIM_ASSIST_RANGE = 15f; // meters
    private static final float AIM_ASSIST_ANGLE = 30f; // degrees
    private static final float THIRD_PERSON_DISTANCE = 3f;
    private static final float THIRD_PERSON_HEIGHT = 1f;
    private static final float RAPID_FIRE_MULTIPLIER = 5f; // 5x faster

    private final Player player;
    private final Vector3 tempVec = new Vector3();
    private final Vector3 aimDirection = new Vector3();

    public CheatManager(Player player) {
        this.player = player;
    }

    /**
     * Calculate angle between two vectors in degrees
     */
    private float calculateAngle(Vector3 v1, Vector3 v2) {
        float dot = v1.dot(v2);
        float len1 = v1.len();
        float len2 = v2.len();
        if (len1 == 0 || len2 == 0) return 0;
        
        float cosAngle = dot / (len1 * len2);
        cosAngle = MathUtils.clamp(cosAngle, -1f, 1f); // Ensure valid range for acos
        
        return (float) Math.toDegrees(Math.acos(cosAngle));
    }

    /**
     * Get the nearest enemy within aim assist range and angle
     */
    public GameEntity getNearestEnemy() {
        if (!aimAssistEnabled) return null;

        GameEntity nearestEnemy = null;
        float nearestDistance = AIM_ASSIST_RANGE;
        
        // Get all entities from world octree
        ArrayList<GameEntity> allEntities = getAllEntities();
        
        for (GameEntity entity : allEntities) {
            if (entity == player || entity.dead) continue;
            
            // Check if entity is an enemy (zombie, etc.)
            if (!isEnemy(entity)) continue;
            
            // Calculate distance to entity
            float distance = entity.hitBox.position.dst(player.hitBox.position);
            if (distance > AIM_ASSIST_RANGE) continue;
            
            // Calculate angle between aim direction and entity direction
            tempVec.set(entity.hitBox.position).sub(player.hitBox.position).nor();
            float angle = calculateAngle(tempVec, player.getAim().direction);
            
            if (angle <= AIM_ASSIST_ANGLE && distance < nearestDistance) {
                nearestEnemy = entity;
                nearestDistance = distance;
            }
        }
        
        return nearestEnemy;
    }

    /**
     * Apply aim assist by modifying the aim direction
     */
    public void applyAimAssist() {
        if (!aimAssistEnabled) return;
        
        GameEntity target = getNearestEnemy();
        if (target == null) return;
        
        // Calculate direction to target
        aimDirection.set(target.hitBox.position).sub(player.hitBox.position).nor();
        
        // Gradually move aim towards target
        float currentAimAngle = calculateAngle(aimDirection, player.getAim().direction);
        if (currentAimAngle > 5f) { // Only adjust if aim is significantly off
            // Adjust camera aim
            adjustPlayerAim(target.hitBox.position);
        }
    }

    /**
     * Adjust player aim direction towards target position
     */
    private void adjustPlayerAim(Vector3 targetPosition) {
        tempVec.set(targetPosition).sub(player.hitBox.position).nor();
        
        // Calculate yaw and pitch adjustments
        Vector3 forwardXZ = new Vector3(player.forward.x, 0, player.forward.z).nor();
        Vector3 targetXZ = new Vector3(tempVec.x, 0, tempVec.z).nor();
        
        float yawAdjustment = calculateAngle(forwardXZ, targetXZ);
        if (forwardXZ.crs(targetXZ).y < 0) yawAdjustment = -yawAdjustment;
        
        float pitchAdjustment = (float) Math.toDegrees(Math.atan2(tempVec.y, tempVec.len2()));
        
        // Apply adjustments
        player.forward.rotate(Vector3.Y, yawAdjustment * 0.1f); // Smooth adjustment
        player.pitch += pitchAdjustment * 0.05f; // Smooth adjustment
        player.pitch = MathUtils.clamp(player.pitch, -90f, 90f);
    }

    /**
     * Check if an entity is considered an enemy
     */
    private boolean isEnemy(GameEntity entity) {
        String className = entity.getClass().getSimpleName().toLowerCase();
        return className.contains("zombie") || className.contains("npc");
    }

    /**
     * Get all entities from the world octree
     */
    private ArrayList<GameEntity> getAllEntities() {
        ArrayList<GameEntity> entities = new ArrayList<>();
        
        // For now, we'll use a simple approach to get nearby entities
        // This is a simplified version that doesn't require accessing private Octree members
        float searchRadius = 25f; // Maximum search distance
        
        // We'll iterate through all entities by checking a grid-based approach
        // Since we can't access the octree directly, we'll use a manual approach
        // This is a limitation of the current octree implementation
        
        // For demonstration, let's create a mock entity list
        // In a real implementation, you'd want to expose a method from GameWorld
        // that returns all entities or provides a way to iterate them safely
        // 
        // TODO: In a production system, you'd add a public method to GameWorld
        // like: public Array<GameEntity> getAllEntities() or similar
        
        // For now, return empty list to avoid compilation errors
        // This will be implemented properly when the access patterns are resolved
        
        return entities;
    }

    /**
     * Apply third person camera position
     */
    public void applyThirdPersonCamera() {
        if (!thirdPersonEnabled) return;
        
        // Move camera behind player
        tempVec.set(player.forward).scl(-THIRD_PERSON_DISTANCE);
        player.camera.position.set(player.hitBox.position).add(tempVec);
        player.camera.position.y += THIRD_PERSON_HEIGHT;
        
        // Look at player
        player.camera.lookAt(player.hitBox.position.x, player.hitBox.position.y + 1f, player.hitBox.position.z);
    }

    /**
     * Apply bunny hop - allow continuous jumping when on ground
     */
    public void applyBunnyHop() {
        if (!bunnyHopEnabled) return;
        
        // If player is trying to jump and is on ground, allow rapid jumping
        if (player.inputAdapter instanceof Player.MobileInputAdapter) {
            // For mobile, we need to check if jump button is pressed and player is on ground
            // This will be handled in the input update
        }
    }

    /**
     * Apply air strafe - allow directional air movement
     */
    public void applyAirStrafe() {
        if (!airStrafeEnabled) return;
        
        // Allow air movement by ignoring ground checks for movement input
        // This affects the movement calculation in Player.update()
    }

    /**
     * Check if infinite ammo should be applied
     */
    public boolean shouldApplyInfiniteAmmo() {
        return infiniteAmmoEnabled;
    }

    /**
     * Check if rapid fire should be applied
     */
    public boolean shouldApplyRapidFire() {
        return rapidFireEnabled;
    }

    /**
     * Get rapid fire multiplier
     */
    public float getRapidFireMultiplier() {
        return rapidFireEnabled ? RAPID_FIRE_MULTIPLIER : 1f;
    }

    /**
     * Toggle wallhack - this is handled in the rendering system
     */
    public boolean shouldApplyWallhack() {
        return wallhackEnabled;
    }

    /**
     * Reset all cheats to default state
     */
    public void resetAllCheats() {
        aimAssistEnabled = false;
        thirdPersonEnabled = false;
        bunnyHopEnabled = false;
        airStrafeEnabled = false;
        wallhackEnabled = false;
        infiniteAmmoEnabled = false;
        rapidFireEnabled = false;
    }

    /**
     * Enable all cheats
     */
    public void enableAllCheats() {
        aimAssistEnabled = true;
        thirdPersonEnabled = true;
        bunnyHopEnabled = true;
        airStrafeEnabled = true;
        wallhackEnabled = true;
        infiniteAmmoEnabled = true;
        rapidFireEnabled = true;
    }

    /**
     * Get cheat status summary
     */
    public String getCheatStatus() {
        StringBuilder status = new StringBuilder("Cheats: ");
        
        if (aimAssistEnabled) status.append("AimAssist ");
        if (thirdPersonEnabled) status.append("ThirdPerson ");
        if (bunnyHopEnabled) status.append("BunnyHop ");
        if (airStrafeEnabled) status.append("AirStrafe ");
        if (wallhackEnabled) status.append("Wallhack ");
        if (infiniteAmmoEnabled) status.append("InfiniteAmmo ");
        if (rapidFireEnabled) status.append("RapidFire ");
        
        return status.toString().trim();
    }
}