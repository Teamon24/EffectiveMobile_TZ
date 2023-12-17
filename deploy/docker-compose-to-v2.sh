
## Docker Compose v2 for Ubuntu 22.04
mkdir -p ~/.docker/cli-plugins/
curl -SL https://github.com/docker/compose/releases/download/v2.17.2/docker-compose-linux-x86_64 -o ~/.docker/cli-plugins/docker-compose
chmod +x ~/.docker/cli-plugins/docker-compose
docker compose version

## You could now set up a shell alias to redirect docker-compose to docker compose.
## This would let you keep using scripts that expect Compose v1, using your new v2 installation.
echo 'alias docker-compose="docker compose"' >> ~/.bashrc
source ~/.bashrc
docker-compose version
