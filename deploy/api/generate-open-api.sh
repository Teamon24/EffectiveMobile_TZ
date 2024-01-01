. ./api/_vars.sh
. ./api/_functions.sh

printProps "generate-open-api.sh" "$PROPS"

cd ..
#Удаление файлов в API-модуле.
./gradlew cleanApiModule $PROPS

#Генерация файлов API-модуля.
./gradlew openApiGenerate $PROPS

#Копируем .gitignore в папку с API-модулем.
./gradlew copyOpenApiGitIgnore $PROPS
