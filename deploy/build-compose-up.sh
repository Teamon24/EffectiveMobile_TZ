. ./vars.sh
. ./functions.sh

echo "build-compose-up.sh: expects 1[$TEST_ARG] 2[$PROFILE_ARGS]; actual: 1[$1] 2[$2] $@"

checkTestArg $1
checkProfileArg $2
shouldBeEmpty $2 2

PROFILE=""
if [ -z "$2" ]; then
    PROFILE="default"
fi
./build.sh "$1"
./compose-up.sh "rmi" "$2"
