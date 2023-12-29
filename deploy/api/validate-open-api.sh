PROFILE=${1:-"default"}
TIME_TO_WAIT=${2:-85}
PROFILE_PROP="-Pprofile=$PROFILE"
TIME_TO_WAIT_PROP="-PtimeToWait=$TIME_TO_WAIT"
PROPS="$PROFILE_PROP $TIME_TO_WAIT_PROP"

cd ..
#Валидация json-файла, который содержит описание API
./gradlew openApiValidate $PROPS

