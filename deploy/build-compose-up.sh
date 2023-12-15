
#1 - пропуск тестов
#2 - профиль
PROFILE=""
echo "build-compose-up.sh: expects 1[$1] 2[$2]; actual: $@"
if [ "$2" = "" ]; then
    PROFILE="default"
fi
./build.sh "$1"
./compose-up.sh "rmi" "$2"
