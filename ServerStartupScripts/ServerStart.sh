#!/bin/bash

cd "$(dirname "$0")"
. ./settings.sh
. ./update.sh

if [ -n "$1" ]
  then
    echo "Using $1"
    cd "$1"
fi

# makes things easier if script needs debugging
if [ x$FTB_VERBOSE = xyes ]; then
    set -x
fi

start_server() {
    if  update_check; then
      update true
    fi
    "$JAVACMD" ${JVM_ARGUMENTS}
}

echo "Starting server"
start_server

