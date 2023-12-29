#!/bin/bash
. ./vars.sh
. ./functions.sh

echo "compose-up.sh: expects 1[rmi/''] 2[$PROFILE_ARGS]; actual: 1[$1] 2[$2] others[$3 $4 $5 $6]"

checkRmiArg $1
checkProfileArg $2
shouldBeEmpty $3 3

ENV_FILE=".env"
PROFILE="$2"
if [ "$1" = 'rmi' ]; then
  docker rmi task-management-system-api
  docker build -t task-management-system-api .
fi

if [ "$2" != "default" ] && [ -n "$2" ]; then
  ENV_FILE="$ENV_FILE.$2"
fi

if [ -z "$PROFILE" ]; then
  PROFILE="default"
fi

ENV_FILE_ARG="--env-file ./profile/$ENV_FILE"
PROFILE_ARG="--profile $PROFILE"

docker compose $ENV_FILE_ARG config
docker compose $ENV_FILE_ARG $PROFILE_ARG up -d --force-recreate
