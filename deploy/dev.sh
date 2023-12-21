. ./deploy/path-vars.sh

PROFILE="dev"
ENV_FILE=".env.$PROFILE"
ENV_FILE_ARG="--env-file $BACK_DIR/profile/$ENV_FILE"
PROFILE_ARG="--profile $PROFILE"

docker compose -f "$BACK_DIR/docker-compose.yml" $ENV_FILE_ARG config
docker compose -f "$BACK_DIR/docker-compose.yml" $ENV_FILE_ARG $PROFILE_ARG up -d

./gradlew run --args='--spring.profiles.active=dev'
