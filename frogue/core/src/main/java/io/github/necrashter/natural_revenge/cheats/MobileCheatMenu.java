package io.github.necrashter.natural_revenge.cheats;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import io.github.necrashter.natural_revenge.Main;
import io.github.necrashter.natural_revenge.world.player.Player;

/**
 * Mobile-friendly cheat menu for Frogue.
 * Provides toggle buttons for all cheat features with clear visual feedback.
 */
public class MobileCheatMenu extends Container<Table> {
    private final CheatManager cheatManager;
    private final Player player;
    
    // UI Components
    private Table mainTable;
    private Button aimAssistButton;
    private Button thirdPersonButton;
    private Button bunnyHopButton;
    private Button airStrafeButton;
    private Button wallhackButton;
    private Button infiniteAmmoButton;
    private Button rapidFireButton;
    private TextButton resetButton;
    private TextButton enableAllButton;
    private TextButton closeButton;
    private Label titleLabel;
    private Label statusLabel;
    private TextButton expandButton;
    
    // Menu state
    private boolean isExpanded = false;
    private static final float MENU_WIDTH = 280f;
    private static final float MENU_HEIGHT = 400f;
    private static final float BUTTON_WIDTH = 250f;
    private static final float BUTTON_HEIGHT = 45f;
    
    public MobileCheatMenu(CheatManager cheatManager, Player player) {
        super();
        this.cheatManager = cheatManager;
        this.player = player;
        
        setupMenu();
    }
    
    private void setupMenu() {
        // Create main table
        mainTable = new Table(Main.skin);
        
        // Create title
        titleLabel = new Label("Frogue Cheats", Main.skin);
        titleLabel.setFontScale(1.2f);
        titleLabel.setColor(Color.YELLOW);
        
        // Create status label
        statusLabel = new Label("All cheats disabled", Main.skin);
        statusLabel.setFontScale(0.8f);
        statusLabel.setColor(Color.LIGHT_GRAY);
        
        // Create expand button (initially visible)
        expandButton = new TextButton("⚙️", Main.skin);
        expandButton.setSize(40f, 40f);
        expandButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleMenu();
            }
        });
        
        setupButtons();
        
        updateStatus();
        
        // Initial collapsed state
        if (isExpanded) {
            showExpandedMenu();
        } else {
            showCollapsedMenu();
        }
    }
    
    private void setupButtons() {
        // Aim Assist Button
        aimAssistButton = createToggleButton("🎯 Aim Assist", "Automatically aim at nearest enemy", aimAssistEnabled -> {
            cheatManager.aimAssistEnabled = aimAssistEnabled;
            updateStatus();
        });
        
        // Third Person Button
        thirdPersonButton = createToggleButton("👁️ Third Person", "Third-person camera view", thirdPersonEnabled -> {
            cheatManager.thirdPersonEnabled = thirdPersonEnabled;
            updateStatus();
        });
        
        // Bunny Hop Button
        bunnyHopButton = createToggleButton("🐰 Bunny Hop", "Continuous jumping on ground", bunnyHopEnabled -> {
            cheatManager.bunnyHopEnabled = bunnyHopEnabled;
            updateStatus();
        });
        
        // Air Strafe Button
        airStrafeButton = createToggleButton("🌪️ Air Strafe", "Directional air movement", airStrafeEnabled -> {
            cheatManager.airStrafeEnabled = airStrafeEnabled;
            updateStatus();
        });
        
        // Wallhack Button
        wallhackButton = createToggleButton("👻 Wallhack", "See enemies through walls", wallhackEnabled -> {
            cheatManager.wallhackEnabled = wallhackEnabled;
            updateStatus();
        });
        
        // Infinite Ammo Button
        infiniteAmmoButton = createToggleButton("💎 Infinite Ammo", "Never run out of ammunition", infiniteAmmoEnabled -> {
            cheatManager.infiniteAmmoEnabled = infiniteAmmoEnabled;
            updateStatus();
        });
        
        // Rapid Fire Button
        rapidFireButton = createToggleButton("⚡ Rapid Fire", "5x faster fire rate", rapidFireEnabled -> {
            cheatManager.rapidFireEnabled = rapidFireEnabled;
            updateStatus();
        });
        
        // Reset Button
        resetButton = new TextButton("Reset All", Main.skin);
        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cheatManager.resetAllCheats();
                refreshAllButtons();
                updateStatus();
            }
        });
        
        // Enable All Button
        enableAllButton = new TextButton("Enable All", Main.skin);
        enableAllButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cheatManager.enableAllCheats();
                refreshAllButtons();
                updateStatus();
            }
        });
        
        // Close Button
        closeButton = new TextButton("Hide Menu", Main.skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showCollapsedMenu();
            }
        });
    }
    
    /**
     * Create a toggle button with callback
     */
    private Button createToggleButton(String text, String description, ToggleCallback callback) {
        Button button = new Button(Main.skin);
        button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        
        Table contentTable = new Table(Main.skin);
        
        Label textLabel = new Label(text, Main.skin);
        textLabel.setFontScale(0.9f);
        textLabel.setColor(Color.WHITE);
        
        Label descLabel = new Label(description, Main.skin);
        descLabel.setFontScale(0.6f);
        descLabel.setColor(Color.LIGHT_GRAY);
        
        contentTable.add(textLabel).left().row();
        contentTable.add(descLabel).left();
        
        button.add(contentTable);
        
        // Add click listener
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean currentState = isButtonEnabled(button);
                boolean newState = !currentState;
                setButtonEnabled(button, newState);
                callback.onToggle(newState);
            }
        });
        
        return button;
    }
    
    /**
     * Toggle menu expansion state
     */
    private void toggleMenu() {
        isExpanded = !isExpanded;
        if (isExpanded) {
            showExpandedMenu();
        } else {
            showCollapsedMenu();
        }
    }
    
    /**
     * Show expanded menu with all options
     */
    private void showExpandedMenu() {
        mainTable.clear();
        
        // Title
        mainTable.add(titleLabel).center().padBottom(10f).row();
        
        // Status
        mainTable.add(statusLabel).center().padBottom(15f).row();
        
        // Cheat buttons
        mainTable.add(aimAssistButton).padBottom(5f).row();
        mainTable.add(thirdPersonButton).padBottom(5f).row();
        mainTable.add(bunnyHopButton).padBottom(5f).row();
        mainTable.add(airStrafeButton).padBottom(5f).row();
        mainTable.add(wallhackButton).padBottom(5f).row();
        mainTable.add(infiniteAmmoButton).padBottom(5f).row();
        mainTable.add(rapidFireButton).padBottom(10f).row();
        
        // Control buttons
        mainTable.add(resetButton).padBottom(5f).row();
        mainTable.add(enableAllButton).padBottom(5f).row();
        mainTable.add(closeButton).row();
        
        setActor(mainTable);
        setSize(MENU_WIDTH, MENU_HEIGHT);
        setVisible(true);
    }
    
    /**
     * Show collapsed menu (just expand button)
     */
    private void showCollapsedMenu() {
        mainTable.clear();
        
        // Just show expand button
        mainTable.add(expandButton).size(60f, 60f);
        
        setActor(mainTable);
        setSize(60f, 60f);
        setVisible(true);
    }
    
    /**
     * Update status text
     */
    private void updateStatus() {
        int enabledCount = 0;
        if (cheatManager.aimAssistEnabled) enabledCount++;
        if (cheatManager.thirdPersonEnabled) enabledCount++;
        if (cheatManager.bunnyHopEnabled) enabledCount++;
        if (cheatManager.airStrafeEnabled) enabledCount++;
        if (cheatManager.wallhackEnabled) enabledCount++;
        if (cheatManager.infiniteAmmoEnabled) enabledCount++;
        if (cheatManager.rapidFireEnabled) enabledCount++;
        
        statusLabel.setText(enabledCount + "/" + 7 + " cheats enabled");
        if (enabledCount == 0) {
            statusLabel.setColor(Color.RED);
        } else if (enabledCount == 7) {
            statusLabel.setColor(Color.GREEN);
        } else {
            statusLabel.setColor(Color.YELLOW);
        }
    }
    
    /**
     * Check if a button is enabled (green)
     */
    private boolean isButtonEnabled(Button button) {
        return button.getColor().r < 0.3f && button.getColor().g > 0.7f;
    }
    
    /**
     * Set button enabled state
     */
    private void setButtonEnabled(Button button, boolean enabled) {
        if (enabled) {
            button.setColor(0.2f, 0.8f, 0.2f, 1f); // Green
        } else {
            button.setColor(0.8f, 0.2f, 0.2f, 1f); // Red
        }
    }
    
    /**
     * Refresh all button states
     */
    private void refreshAllButtons() {
        setButtonEnabled(aimAssistButton, cheatManager.aimAssistEnabled);
        setButtonEnabled(thirdPersonButton, cheatManager.thirdPersonEnabled);
        setButtonEnabled(bunnyHopButton, cheatManager.bunnyHopEnabled);
        setButtonEnabled(airStrafeButton, cheatManager.airStrafeEnabled);
        setButtonEnabled(wallhackButton, cheatManager.wallhackEnabled);
        setButtonEnabled(infiniteAmmoButton, cheatManager.infiniteAmmoEnabled);
        setButtonEnabled(rapidFireButton, cheatManager.rapidFireEnabled);
    }
    
    /**
     * Update menu position to top-right corner
     */
    public void updatePosition() {
        setPosition(1280f - MENU_WIDTH - 20f, 720f - 20f); // Top-right corner
    }
    
    /**
     * Interface for toggle callbacks
     */
    private interface ToggleCallback {
        void onToggle(boolean enabled);
    }
}