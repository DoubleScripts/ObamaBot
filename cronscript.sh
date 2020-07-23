
dir=$(pwd)

cd ./ServerStartupScripts/

echo "Using $(pwd)"

. ./update.sh
cd "$dir"

updateAndRestart