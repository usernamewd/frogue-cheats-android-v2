# Android CI/CD Setup for Frogue

This directory contains the GitHub Actions workflow and configuration files needed to automatically build Android APKs for the Frogue game.

## Files Created

### 1. `.github/workflows/android-build.yml`
Main GitHub Actions workflow that:
- Triggers on pushes to main/master branches and pull requests
- Builds Android APK automatically
- Uploads APK and AAB artifacts
- Runs unit tests
- Uses JDK 17 and Android SDK

### 2. `android/gradle.properties`
Configuration file with build settings optimized for CI/CD:
- Memory settings for faster builds
- AndroidX and Jetifier enabled
- Build cache optimizations
- CI/CD specific Gradle daemon settings

### 3. `generate_debug_keystore.sh`
Script to generate debug keystore for signing Android builds:
```bash
./generate_debug_keystore.sh
```

### 4. `android_signing_config.gradle`
Signing configuration template to add to `android/app/build.gradle`:
- Debug and release signing configs
- Automatic keystore detection
- Environment-based signing

## Setup Instructions

### Step 1: Add Signing Configuration

Add the contents of `android_signing_config.gradle` to your `android/app/build.gradle` file inside the `android { }` block:

```gradle
android {
    // ... existing configurations ...
    
    signingConfigs {
        debug {
            if (project.hasProperty('android.keystorePassword')) {
                storeFile file(project.findProperty('android.keystoreFile') ?: 'debug.keystore')
                storePassword project.findProperty('android.keystorePassword') ?: 'android'
                keyAlias project.findProperty('android.keyAlias') ?: 'androiddebugkey'
                keyPassword project.findProperty('android.keyPassword') ?: 'android'
            } else {
                storeFile new File(System.getenv('HOME'), '.android/debug.keystore')
                storePassword 'android'
                keyAlias 'androiddebugkey'
                keyPassword 'android'
            }
        }
        
        release {
            if (project.hasProperty('android.keystoreFile')) {
                storeFile file(project.findProperty('android.keystoreFile'))
                storePassword project.findProperty('android.keystorePassword')
                keyAlias project.findProperty('android.keyAlias')
                keyPassword project.findProperty('android.keyPassword')
            }
        }
    }
    
    buildTypes {
        debug {
            minifyEnabled false
            debuggable true
            signingConfig signingConfigs.debug
            buildConfigField "boolean", "IS_PREMIUM", "true"
            applicationIdSuffix ".debug"
            versionNameSuffix "-debug"
        }
        
        release {
            minifyEnabled false
            debuggable false
            signingConfig signingConfigs.release
            buildConfigField "boolean", "IS_PREMIUM", "true"
        }
    }
}
```

### Step 2: Generate Debug Keystore

Run the keystore generation script once:
```bash
./generate_debug_keystore.sh
```

This creates a `debug.keystore` file with these credentials:
- Keystore password: `android`
- Key alias: `androiddebugkey`
- Key password: `android`
- Certificate: Android Debug certificate

### Step 3: Commit and Push

Commit all the new files:
```bash
git add .github/ android/ generate_debug_keystore.sh android_signing_config.gradle
git commit -m "Add Android CI/CD build pipeline"
git push
```

### Step 4: Test the Build

The workflow will automatically trigger on your next push or pull request. Check the "Actions" tab in your GitHub repository to monitor build progress.

## Workflow Features

### Build Process
1. **Environment Setup**: JDK 17, Android SDK, Gradle
2. **Dependency Caching**: Faster subsequent builds
3. **APK Generation**: Creates both APK and AAB formats
4. **Artifact Upload**: Builds are saved for 30 days
5. **Testing**: Runs unit tests (if configured)

### Build Outputs
- **APK File**: `android/build/outputs/apk/release/android-release.apk`
- **AAB File**: `android/build/outputs/bundle/release/android-release.aab`

### Trigger Conditions
- Push to `main`, `master`, or `develop` branches
- Pull requests to these branches
- Manual trigger (configure in workflow settings)

## Customization

### Release Signing
For production releases, create a proper signing keystore:
```bash
keytool -genkey -v \
    -keystore my-release-key.keystore \
    -alias my-key-alias \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000
```

Then add secrets to your GitHub repository:
- `KEYSTORE_FILE`: Base64 encoded keystore content
- `KEYSTORE_PASSWORD`: Keystore password
- `KEY_ALIAS`: Key alias
- `KEY_PASSWORD`: Key password

### Different Build Configurations
Modify the workflow to use different Gradle tasks:
- `:android:assembleDebug` for debug builds
- `:android:assembleRelease` for release builds
- `:android:bundleRelease` for app bundle builds

### Android SDK Versions
Update the API level in the workflow:
```yaml
with:
  api-level: 33  # Android 13
  build-tools-version: 33.0.2
  target: default
```

## Troubleshooting

### Common Issues
1. **Build failures**: Check Gradle wrapper permissions (`chmod +x gradlew`)
2. **Signing errors**: Ensure keystore files are properly generated and referenced
3. **Memory issues**: Increase JVM heap size in gradle.properties
4. **SDK issues**: Verify Android SDK setup in workflow

### Build Logs
Check the Actions tab for detailed build logs and error messages.

### Local Testing
Test builds locally before pushing:
```bash
./gradlew :android:assembleRelease
```

## Security Notes

- Never commit release keystores to the repository
- Use GitHub Secrets for production signing keys
- Debug keystores are safe for public repositories
- Monitor artifact retention policies

The CI/CD pipeline is now ready to automatically build Android APKs for your Frogue game!