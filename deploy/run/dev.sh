PROFILE="dev"
ENV_FILE=".env.$PROFILE"
ENV_FILE_ARG="--env-file ./profile/$ENV_FILE"
PROFILE_ARG="--profile $PROFILE"

docker compose $ENV_FILE_ARG config
docker compose $ENV_FILE_ARG $PROFILE_ARG up -d
CUR_DIR=$(pwd)
cd ..
./gradlew run --args='--spring.profiles.active=dev'
cd "$CUR_DIR"
