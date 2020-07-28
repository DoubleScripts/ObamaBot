export JAR_VER="1.0-SNAPSHOT"
export JARFILE="Obama-${JAR_VER}.jar"
export JAVACMD="java"

export SERVICE_NAME="obamabot"

# By default, this uses Aikar's flags as of 7/16/2020. Change or remove them if needed
export RAM_USAGE="2G"
export JVM_ARGUMENTS="-Xms${RAM_USAGE} -Xmx${RAM_USAGE} -jar ${JARFILE}"

export VERSION_LIST="https://api.github.com/repos/antaxiom/ObamaBot/commits/master"

export UPDATE_FILE="current_version.txt"
