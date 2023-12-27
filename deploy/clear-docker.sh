cat ~/.docker/config.json
cp ~/.docker/config.json ~/.docker/config-backup.json
cat ~/.docker/config-backup.json
rm ~/.docker/config.json
sudo rm -rf ~/.docker/buildx

docker stop $(docker ps -aq)
docker rm $(docker ps -aq)
docker rmi $(docker images -q)

docker system prune --all --force --volumes