. ./api/vars.sh

cd ..
./gradlew deleteDocsJson $PROPS
./gradlew cleanApiModule $PROPS

#Генерация json-файла, который содержит описание API
./gradlew generateOpenApiDocs $PROPS --debug
