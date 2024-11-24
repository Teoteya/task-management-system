# Базовый образ Java 17. Указываем тот же образ, что и в image: внутри docker-compose.yml
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR файл
COPY target/task-management-system-0.0.1-SNAPSHOT.jar task-management-system-0.0.1-SNAPSHOT.jar

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "myapp.jar"]
