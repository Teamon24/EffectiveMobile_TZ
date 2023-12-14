docker-compose stop
docker-compose rm -f
echo "compose-up.sh: $1"
if [ "$1" = 'rmi' ]; then
  docker rmi task-management-system-image
  docker build -t task-management-system-image .
fi

docker-compose -f docker-compose.yml up -d
