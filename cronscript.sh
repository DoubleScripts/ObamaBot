#!/bin/bash

if [ -n "$1" ]
  then
    cd "$1"
fi

dir=$(pwd)

cd ./ServerStartupScripts/

echo "Using $(pwd)"

. ./update.sh
cd "$dir"

updateAndRestart