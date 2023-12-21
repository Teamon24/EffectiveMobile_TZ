. ./deploy/path-vars.sh
. ./deploy/scripts/base/functions.sh

ARGS=" $@ "
EXTENSIONS_YAML=""
CURATOR="curator"
LOGSPOUT="logspout"
METRICBEAT="metricbeat"
FILEBEAT="filebeat"

echo "ELK_DIR: $ELK_DIR"
echo "EXT_DIR: $EXT_DIR"

if [[ "$ARGS" == *" rm "* ]]; then
  docker container rm -f $(containersIdsOf "elk-")
fi

if [[ "$ARGS" == *" rmi "* ]]; then
  docker container rm -f $(containersIdsOf "elk-")
  docker rmi $(imagesIdsOf "elk-")
fi

if [[ "$ARGS" == *" $FILEBEAT "* ]]; then
  EXTENSIONS_YAML+=" -f $EXT_DIR/filebeat/filebeat-compose.yml "
fi

if [[ "$ARGS" == *" $CURATOR "* ]]; then
  EXTENSIONS_YAML+=" -f $EXT_DIR/curator/curator-compose.yml "
fi

if [[ "$ARGS" == *" $LOGSPOUT "* ]]; then
  echo "Running elk with extension '$LOGSPOUT' is not supported yet" 1>&2; echo "Try to run extensions one by one" 1>&2; exit 64
  EXTENSIONS_YAML+=" -f $EXT_DIR/logspout/logspout-compose.yml "
fi

if [[ "$ARGS" == *" $METRICBEAT "* ]]; then
  echo "Running elk with extension '$LOGSPOUT' is not supported yet" 1>&2; echo "Try to run extensions one by one" 1>&2; exit 64
  EXTENSIONS_YAML+=" -f $EXT_DIR/metricbeat/metricbeat-compose.yml "
fi

if [[ "$ARGS" == *" config-only "* ]]; then
  docker compose -f $ELK_DIR/elk-compose.yml $EXTENSIONS_YAML --env-file $ELK_DIR/.env.docker.hub  config
  exit 0
fi

if [[ "$ARGS" == *" show-config "* ]]; then
  docker compose -f $ELK_DIR/elk-compose.yml $EXTENSIONS_YAML --env-file $ELK_DIR/.env.docker.hub  config
fi

docker compose -f $ELK_DIR/elk-compose.yml $EXTENSIONS_YAML --env-file $ELK_DIR/.env.docker.hub  up -d


