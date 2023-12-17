# Запуск API "Система управления задачами"

### ./entrypoints/default.sh
    Запуск проекта в default-профиле: 
    - запускается при помощи docker-compose
    - api и база данных запускается при помощи gradle-задачи

### ./entrypoints/dev.sh
    Запуск проекта в dev-профиле: 
    - база данных запускается при помощи docker-compose
    - проект запускается при помощи gradle-задачи

### build.sh
    Сборка проекта (собирается исполняемый jar):
    - аргумент "-x test": - запуск без прогонки тестов

### compose-up.sh
    Запуск проекта при помощи docker-compose:
    - аргумент rmi: пересборка docker-образов
    - аргумент dev/default/<empty>=default: профиль приложения

### build-compose-up.sh
    Сборка и запуск проекта в docker-контейнере при помощи docker-compose:
    - аргумент "-x test": запуск без прогонки тестов 
    - аргумент dev/default/<empty>=default: профиль приложения  

### Документация
[swagger ui](http://localhost:8008/swagger-ui/index.html)
    
[json-формат](http://localhost:8008/v3/api-docs)

### Готовые запросы
[Коллекция запросов в Postman](https://www.postman.com/eom-back/workspace/task-management-system/collection/2929901-3c1a7865-4b0c-4dab-95fc-15d55ede2119?action=share&creator=2929901&active-environment=2929901-f19febb9-39d0-46c2-803e-04d60dcf2c28)
