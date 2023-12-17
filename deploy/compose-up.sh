echo "compose-up.sh: expects 1[rmi/''] 2[profiles:dev/default/''] 3[dev/default/'']; actual: 1[$1] 2[$2] all[$@]"

ENV_FILE=".env"
PROFILE="$2"
if [ "$2" != 'default' ] | [ "$2" != '' ]; then
  ENV_FILE="$ENV_FILE.$2"
fi

if [ "$PROFILE" == '' ]; then
  PROFILE="default"
fi

ENV_FILE_ARG="--env-file ./profile/$ENV_FILE"
PROFILE_ARG="--profile $PROFILE"

if [ "$1" = 'rmi' ]; then
  docker rmi task-management-system-image
  docker build -t task-management-system-image .
fi

echo "docker-compose $ENV_FILE_ARG config"
docker compose $ENV_FILE_ARG config
echo "docker-compose $ENV_FILE_ARG $PROFILE_ARG -f docker-compose.yml up -d"
docker compose $ENV_FILE_ARG $PROFILE_ARG up -d --force-recreate
