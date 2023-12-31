PROFILE=${1:-"default"}
TIME_TO_WAIT=${2:-85}
API_JAVA_VERSION=${3:-"8"}

DELIM=";"
JAVA_VERSIONS="8${DELIM}9${DELIM}11${DELIM}17"

MAIN_CLASS='org.effective_mobile.task_management_system.maintain.docs.generation.OpenApiGeneration'

PROPS=""
PROPS="$PROPS -Pprofile=$PROFILE"
PROPS="$PROPS -PtimeToWait=$TIME_TO_WAIT"
PROPS="$PROPS -PapiModuleJdk=$API_JAVA_VERSION"
PROPS="$PROPS -Pentrypoint=$MAIN_CLASS"
