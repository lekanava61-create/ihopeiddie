#!/bin/bash

# Gradle wrapper script
# Download and run Gradle if not already present

GRADLE_VERSION=7.6.1
GRADLE_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
GRADLE_HOME=".gradle"

if [ ! -d "$GRADLE_HOME/gradle-$GRADLE_VERSION" ]; then
    mkdir -p "$GRADLE_HOME"
    echo "Downloading Gradle $GRADLE_VERSION..."
    curl -L "$GRADLE_URL" -o "$GRADLE_HOME/gradle.zip"
    unzip -q "$GRADLE_HOME/gradle.zip" -d "$GRADLE_HOME"
    rm "$GRADLE_HOME/gradle.zip"
fi

export PATH="$GRADLE_HOME/gradle-$GRADLE_VERSION/bin:$PATH"
gradle "$@"
