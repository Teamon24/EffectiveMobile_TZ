#!/bin/bash
. ./deploy/path-vars.sh
. ./deploy/scripts/base/vars.sh
. ./deploy/scripts/base/functions.sh

echo "compose-up.sh: expects [rmi/''] [$PROFILE_ARGS] [show-config]; actual: [$1] [$2] [$3] others[$4 $5 $6]"

shouldBeEmpty $3 3

ARGS=" $@ "
ENV_FILE=".env"
PROFILE="default"

if [[ "$ARGS" == *" rmi "* ]]; then
  docker container rm -f $(containersIdsOf "task-management-system-api")
  docker rmi $(imagesIdsOf "task-management-system-api")
  docker build -t task-management-system-api-image $BACK_DIR
fi

if [[ "$ARGS" == *" default "* ]]; then
  ENV_FILE="$ENV_FILE"
fi

if [[ "$ARGS" == *" dev "* ]]; then
  ENV_FILE="$ENV_FILE.dev"
  PROFILE="dev"
fi

ENV_FILE_ARG="--env-file $BACK_DIR/profile/$ENV_FILE"
PROFILE_ARG="--profile $PROFILE"

if [[ "$ARGS" == *" config "* ]]; then
  docker compose -f $BACK_DIR/docker-compose.yml $ENV_FILE_ARG $PROFILE_ARG config
fi

docker compose -f $BACK_DIR/docker-compose.yml $ENV_FILE_ARG $PROFILE_ARG up -d --force-recreate
