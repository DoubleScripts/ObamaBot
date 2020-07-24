#!/bin/bash

. ./settings.sh

latest_version="null"

check_libraries() {
    if ! command -v jq &> /dev/null
    then
        echo "jq not found. jq is required for JSON parsing"
        exit
    fi

    if ! command -v ${JAVACMD} &> /dev/null
    then
        echo "Java command \"${JAVACMD}\" not found. It is required for running the server"
        exit
    fi
}

update_check() {
  check_libraries

  current_version="null"

  if [ ! -f "${UPDATE_FILE}" ]; then
    echo ${current_version} > "${UPDATE_FILE}"
  else
    current_version=$(<${UPDATE_FILE})
  fi

  latest_version=$(curl -JLs "${VERSION_LIST}" | jq -r .sha) # Gets the build key of the JSON result

  echo "USING VERSION ${current_version} WHILE LATEST IS ${latest_version}"

  if [ ! "${current_version}" == "${latest_version}" ] ; then

    return 0
  fi

  return 1
}

update() {
  copyJar=$1

  echo "UPDATING TO NEW VERSION"

   if ! git pull ; then
    echo Unable to pull from git repo
    return 1
  fi

  if ! buildJar $copyJar ; then
    echo "FAILED TO DOWNLOAD LATEST VERSION OF MINECRAFT SERVER"
    return 1
  else
    echo "${latest_version}" > "${UPDATE_FILE}"
    return 0
  fi
}

buildJar() {
  copyJar=$1

  mvn clean package

  mvnResult="$?"

  if [ ! $mvnResult -eq 0 ] ; then
    >&2 echo "Unable to build jar. Malformed java code? Running with previous version"
  else
    if [ "$copyJar" ] ; then
      cp ./target/"${JARFILE}" ./"${JARFILE}"
    fi
  fi

  echo "$mvnResult is result"

  return $mvnResult
}

checkService() {
  if P=$(systemctl is-active --quiet obamabot)
  then
    echo "start"
  else
    echo "stop"
  fi
}

updateAndRestart() {
  serviceStatus=$(checkService "${SERVICE_NAME}")

  if update_check ; then


      if [ "$(update false)" ] ; then

        sudo service "${SERVICE_NAME}" stop

        cp ./target/"${JARFILE}" ./"${JARFILE}"

        sudo service "${SERVICE_NAME}" "${serviceStatus}"

      fi
  fi
}