HOST=${1:-"localhost"}
PORT=$2
CONTENT_TYPE="Content-Type: application/json"
USERNAME="teamon24"
EMAIL="$USERNAME@gmail.com"
PASSWORD="1As!@asfasf2323"
ADDRESS="$HOST:$PORT"
curl -L "$ADDRESS/signup" -H "$CONTENT_TYPE" --data-raw "{\"email\": \"$EMAIL\",\"username\":\"$USERNAME\",\"password\": \"$PASSWORD\"}"
curl -L "$ADDRESS/signin" -H "$CONTENT_TYPE" --data-raw "{\"email\": \"$EMAIL\", \"password\":\"$PASSWORD\"}"
