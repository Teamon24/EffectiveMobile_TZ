ARGS=" $@ "
EXTENSIONS_YAML=""
EXT_ENV_FILE="$2"
if [[ "$ARGS" == *" ext "* ]]; then
    echo "Running elk with extensions is not supported yet" 1>&2
    echo "Try to run extensions one by one" 1>&2
    exit 64
  EXTENSIONS_YAML+=" -f ./elk/extensions/curator/curator-compose.yml"
  EXTENSIONS_YAML+=" -f ./elk/extensions/filebeat/filebeat-compose.yml"
  EXTENSIONS_YAML+=" -f ./elk/extensions/logspout/logspout-compose.yml"
  EXTENSIONS_YAML+=" -f ./elk/extensions/metricbeat/metricbeat-compose.yml"
fi

if [[ "$ARGS" == *" rmi "* ]]; then
  echo "elk.sh: rmi option executing"
  docker container rm -f $(docker container ls -q --filter name=elk-)
  docker rmi $(docker container ls -q --filter name=elk-)
fi

echo "$EXTENSIONS_YAML"
echo "elk.sh: docker compose -f ./elk/elk-compose.yml $EXTENSIONS_YAML --env-file ./elk/.env.docker.hub  up -d"
docker compose -f ./elk/elk-compose.yml $EXTENSIONS_YAML --env-file ./elk/.env.docker.hub  up -d