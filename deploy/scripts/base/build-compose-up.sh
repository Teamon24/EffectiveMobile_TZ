. ./deploy/path-vars.sh
. ./deploy/scripts/base/vars.sh
. ./deploy/scripts/base/functions.sh

echo "build-compose-up.sh: expects 1[$TEST_ARG] 2[$PROFILE_ARGS]; actual: 1[$1] 2[$2] others [$3 $4 $5 $6]"

checkTestArg $1
checkProfileArg $2
shouldBeEmpty $2 2

PROFILE=""
if [ -z "$2" ]; then
    PROFILE="default"
fi
$BACK_DIR/scripts/base/build.sh "$1"
$BACK_DIR/scripts/base/compose-up.sh "rmi" "$2"
