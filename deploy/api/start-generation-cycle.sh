. ./api/_vars.sh
. ./api/_functions.sh

printProps "start-generation-cycle.sh" "$PROPS"

./api/generate-open-api-specification.sh
./api/generate-open-api.sh
./api/compile-open-api.sh
