#!/bin/bash

if [ $TRAVIS_OS_NAME = 'osx' ]; then
  gem install xcpretty
  brew install yarn
  npm install -g ionic@4.1.2 phonegap plugman cordova@6.5.0
else
  yarn global add ionic@4.1.2 phonegap plugman cordova@6.5.0
  mkdir -p $ANDROID_HOME/licenses
  echo -e "d56f5187479451eabf01fb78af6dfcb131a6481e" >> $ANDROID_HOME/licenses/android-sdk-license
  echo -e "84831b9409646a918e30573bab4c9c91346d8abd" >> $ANDROID_HOME/licenses/android-sdk-preview-license
fi
