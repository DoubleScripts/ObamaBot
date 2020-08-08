#!/bin/bash

if [ -n "$1" ]
  then
    cd "$1"
fi

dir=$(pwd)

cd ./ServerStartupScripts/

echo "Using $(pwd)"


echo Install Gradle
sudo apt install gradle


. ./update.sh
cd "$dir"

updateAndRestart
