CONTAINER_NAME=$1
docker exec -it $(docker container ls -q --filter name=$CONTAINER_NAME) /bin/bash