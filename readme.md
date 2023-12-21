# Запуск API "Система управления задачами"
    Запуск скриптоа произовдится из коренной папки проекта (папка, которая содержит папку /deploy)
 
### ./deploy/default.sh
    Запуск проекта в default-профиле: 
    - запускается при помощи docker-compose
    - api и база данных запускается при помощи gradle-задачи

### ./deploy/dev.sh
    Запуск проекта в dev-профиле: 
    - база данных запускается при помощи docker-compose
    - проект запускается при помощи gradle-задачи

### ./deploy/elk/elk.sh
    Запуск elk: 
    - аргумент: rm/<empty> - удаление контейнеров, которые относятся к elk.
    - аргумент: rmi/<empty> - пересборка docker-образов, которые относятся к elk.
    - аргумент: filebeat - запуска расширения filebeat.
    - аргумент: metricbeat - запуска расширения metricbeat.


### ./deploy/scripts/base/build.sh
    Сборка проекта (собирается исполняемый jar):
    - аргумент "tests": - запуск с прогонкой тестов

### ./deploy/scripts/base/compose-up.sh
    Запуск проекта при помощи docker-compose:
    - аргумент: rmi/<empty> - пересборка docker-образа
    - аргумент: dev/default/<empty> - запуск кон

### ./deploy/scripts/base/build-compose-up.sh
    Сборка и запуск проекта в docker-контейнере при помощи docker-compose:
    - аргумент "tests": - запуск с прогонкой тестов
    - аргумент dev/default/<empty>=default: профиль приложения  


### Документация
[swagger ui](http://localhost:9000/swagger-ui/index.html)
    
[json-формат](http://localhost:9000/v3/api-docs)

### Готовые запросы
[Коллекция запросов в Postman](https://www.postman.com/eom-back/workspace/task-management-system/collection/2929901-3c1a7865-4b0c-4dab-95fc-15d55ede2119?action=share&creator=2929901&active-environment=2929901-f19febb9-39d0-46c2-803e-04d60dcf2c28)
