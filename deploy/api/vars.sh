PROFILE=${1:-"default"}
TIME_TO_WAIT=${2:-'85'}
API_MODULE_JAVA_VERSION=${3:-'17'}

DELIM=";"
JAVA_VERSIONS="8${DELIM}9${DELIM}11${DELIM}17"

MAIN_CLASS='org.effective_mobile.task_management_system.maintain.docs.generation.OpenApiGeneration'

PROPS=""
PROPS="$PROPS -Pcustom.profile=$PROFILE"
PROPS="$PROPS -Pcustom.timeToWait=$TIME_TO_WAIT"
PROPS="$PROPS -Pcustom.apiModuleJdk=$API_MODULE_JAVA_VERSION"
PROPS="$PROPS -Pcustom.entrypoint=$MAIN_CLASS"

MVN_HOME_=""
if [ $API_MODULE_JAVA_VERSION = '8' ]; then
  MVN_HOME_='/opt/apache-maven-3.6.3/bin/mvn'
else
  MVN_HOME_='/opt/apache-maven-3.8.4/bin/mvn'
fi

PROPS="$PROPS -Pcustom.mvnHome=$MVN_HOME_"