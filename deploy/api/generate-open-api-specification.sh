. ./api/_vars.sh

cd ..

#Удаление предыдущей спецификации API
./gradlew deleteApiSpecification $PROPS

#Генерация спецификации API, по аннотированному коду в main-модуле
./gradlew generateOpenApiDocs $PROPS

#Валидация спецификации API
./gradlew openApiValidate $PROPS
