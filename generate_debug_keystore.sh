#!/bin/bash
# Script to generate debug keystore for Android builds

KEYSTORE_PATH="debug.keystore"
KEYSTORE_PASSWORD="android"
KEY_ALIAS="androiddebugkey"
KEY_PASSWORD="android"
KEY_DN="CN=Android Debug,O=Android,C=US"

echo "Generating debug keystore for Android builds..."

# Generate keystore if it doesn't exist
if [ ! -f "$KEYSTORE_PATH" ]; then
    keytool -genkey -v \
        -keystore "$KEYSTORE_PATH" \
        -alias "$KEY_ALIAS" \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        -storepass "$KEYSTORE_PASSWORD" \
        -keypass "$KEY_PASSWORD" \
        -dname "$KEY_DN" \
        -noprompt
    
    echo "Debug keystore generated successfully: $KEYSTORE_PATH"
    echo "Keystore password: $KEYSTORE_PASSWORD"
    echo "Key alias: $KEY_ALIAS"
    echo "Key password: $KEY_PASSWORD"
else
    echo "Debug keystore already exists: $KEYSTORE_PATH"
fi