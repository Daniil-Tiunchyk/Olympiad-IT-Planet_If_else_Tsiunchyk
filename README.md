# Climatica API

Это приложение, созданное на Java Spring, предназначено для предоставления точных и актуальных климатических данных.

## Архитектура приложения

Climatica API следует стандартной архитектуре Spring Boot приложения с контроллерами, сервисами, репозиториями и моделями данных.

## Предварительные требования

- Java JDK 17 или новее
- Docker и Docker Compose (для запуска через Docker)
- PostgreSQL (для локального запуска)

## Запуск приложения через Docker

1. Клонировать репозиторий и перейти в папку `IT-Planet-stage-2`:
   ```
   git clone https://github.com/Daniil-Tsiunchyk/IT-Planet_If_else_Tsiunchyk/
   cd IT-Planet-stage-2
   ```

2. Запустить приложение:
   ```
   docker-compose up -d
   ```

Swagger доступен по адресу: http://localhost:8080/api/

## Локальный запуск (без Docker)

1. Убедитесь, что PostgreSQL запущен и создайте базу данных `climatica`.

2. Настройте строку подключения в файле `application.properties`:
   ```
   spring.datasource.url=jdbc:postgresql://localhost:5432/climatica
   spring.datasource.username=postgres
   spring.datasource.password=root
   ```

3. Соберите и запустите приложение с помощью Maven:
   ```
   mvn clean install
   mvn spring-boot:run
   ```

Swagger доступен по адресу: http://localhost:8080/api/

## Лицензия

Проект распространяется под лицензией MIT. Подробности смотрите в файле LICENSE.
