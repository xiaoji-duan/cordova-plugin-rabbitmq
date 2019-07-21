#!/bin/bash

if [ $TRAVIS_OS_NAME = 'osx' ]; then
    # Build on macOS
    cordova build ios
else
    # Build on Linux
    cordova build android -- --gradleArg=-Pandroid.compileOptions.sourceCompatibility=1.8 --gradleArg=-Pandroid.compileOptions.targetCompatibility=1.8 --gradleArg=-Pandroid.enableD8.desugaring=false
fi
