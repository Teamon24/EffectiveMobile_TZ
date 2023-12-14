# Запуск API "Система управления задачами"

### run-jar.sh "-x test"
    Запуск проекта: 
    - аргумент: "-x test" - запуск без прогонки тестов
    - база данных запускается при помощи docker-compose
    - проект запускается при помощи gradle-задачи

### build.sh "-x test"
    Сборка проекта:
    - аргумент: "-x test" - запуск без прогонки тестов
    - собирается fat jar

### compose-up.sh
    Запуск проекта при помощи docker-compose:
    - аргумент: rmi - пересборка docker-образа

### build-compose-up.sh "-x test"
    Сборка и запуск проекта в docker-контейнере при помощи docker-compose:
    - аргумент: "-x test" - запуск без прогонки тестов

### Документация
[swagger ui](http://localhost:8080/swagger-ui/index.html)
    
[json-формат](http://localhost:8080/v3/api-docs)

### Готовые запросы
[Коллекция запросов в Postman](https://www.postman.com/eom-back/workspace/task-management-system/collection/2929901-3c1a7865-4b0c-4dab-95fc-15d55ede2119?action=share&creator=2929901&active-environment=2929901-f19febb9-39d0-46c2-803e-04d60dcf2c28)
