CUR_DIR=$(pwd)
echo "build.sh: expects 1[$1]; actual: $@"
cd ..
./gradlew deleteJar
./gradlew clean build $@
./gradlew bootJar
./gradlew copyJar
cd "$CUR_DIR"
