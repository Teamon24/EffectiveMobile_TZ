. ./api/vars.sh

echo "generate-open-api: PROPS=$PROPS"
cd ..
./gradlew deleteDocsJson $PROPS
./gradlew cleanApiModule $PROPS

#Генерация json-файла, который содержит описание API
./gradlew generateOpenApiDocs $PROPS --stacktrace

#Валидация json-файла, который содержит описание API
./gradlew openApiValidate $PROPS

#Генерация самого API
./gradlew openApiGenerate $PROPS

#Копируем .gitignore в папку с API-модулем
./gradlew copyOpenApiGitIgnore $PROPS

./gradlew compileApiModule $PROPS

