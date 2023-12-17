. ./vars.sh
. ./functions.sh

CUR_DIR=$(pwd)
echo "build.sh: expects 1[$TEST_ARG]; actual: $@"

checkTestArg $1
shouldBeEmpty $2 2

if [ -z "$1" ];then
  tests="-x test"
fi

if [ -n "$1" ];then
  tests=""
fi

cd ..
./gradlew deleteJar
echo "./gradlew clean build $tests"
./gradlew clean build $tests
./gradlew bootJar
./gradlew copyJar
cd "$CUR_DIR"
