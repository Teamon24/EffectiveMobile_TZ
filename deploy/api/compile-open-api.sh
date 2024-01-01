. ./api/vars.sh
. ./api/functions.sh

isANumber "$API_JAVA_VERSION" "java version"

if existsInList "$JAVA_VERSIONS" "$DELIM" "$API_JAVA_VERSION"; then
  echo "$API_JAVA_VERSION in acceptable versions: [$JAVA_VERSIONS]"
else
  echo "$API_JAVA_VERSION not in acceptable versions: [$JAVA_VERSIONS]"; exit 1
fi

cd ..
./gradlew compileApiModule $PROPS --stacktrace
