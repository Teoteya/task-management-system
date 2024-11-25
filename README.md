## Описание
Task Management System — это приложение для управления задачами на Java с использованием Spring Boot, PostgreSQL, Docker и Docker Compose. Этот документ содержит инструкции по локальному запуску проекта.

## Технологический стек
- **Java**: 17
- **Spring Boot**: 2.7.0
- **PostgreSQL**: 17.0
- **Docker & Docker Compose**
- **Git**

## Системные требования
- Свободный порт `5432` для PostgreSQL
- Свободный порт `8080` для приложения

## Шаги для локального запуска проекта
1. Склонируйте репозиторий проекта:
git clone https://github.com/Teoteya/task-management-system.git

2. Запустите PostgreSQL и создайте базу данных:
CREATE DATABASE task_management;

Для подключения к базе данных используйте следующие параметры:

URL: jdbc:postgresql://localhost:5432/task_management

Имя пользователя: postgres

Пароль: 12345

3. Соберите проект с помощью Maven:
mvn clean package 

После успешной сборки в папке target появится JAR-файл:
task-management-system-0.0.1-SNAPSHOT.jar

4. Запустите Docker. Затем выполните команду для сборки Docker-образа:
docker-compose build

5. Запустите Docker Compose в фоновом режиме. Запустите контейнеры (приложение и базу данных):
docker-compose up

После успешного запуска приложение будет доступно по адресу http://localhost:8080.

6. Чтобы остановить приложение и удалить контейнеры, выполните:
docker-compose down

## Документация API.
Swagger UI:
После запуска приложения вы можете открыть Swagger UI для тестирования API: 
http://localhost:8080/swagger-ui.html
Документация API: http://localhost:8080/v3/api-docs
