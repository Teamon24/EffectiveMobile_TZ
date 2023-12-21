. ./deploy/path-vars.sh
. ./deploy/path-functions.sh

. ./deploy/scripts/base/vars.sh
. ./deploy/scripts/base/functions.sh

echo "srcdir=$SRC_DIR"
echo "build.sh: expects 1[$TEST_ARG]; actual: $@"

checkTestArg $1
shouldBeEmpty $2 2

if [ -z "$1" ];then
  tests="-x test"
fi

if [ -n "$1" ];then
  tests=""
fi

./gradlew deleteJar
./gradlew clean build $tests
./gradlew bootJar
./gradlew copyJar
