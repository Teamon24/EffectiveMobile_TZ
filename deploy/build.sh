
CUR_DIR=$(pwd)
echo "build.sh: $@"
cd ..
./gradlew deleteJar
./gradlew clean build $@
./gradlew fatJar
./gradlew copyJar
cd "$CUR_DIR"