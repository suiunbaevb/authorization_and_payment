# Payment API

**REST-сервис для обработки платежей с аутентификацией через JWT и документацией Swagger**

---

## Содержание

1. [Описание проекта](#описание-проекта)
2. [Технологии](#технологии)
3. [Требования](#требования)
4. [Установка и запуск](#установка-и-запуск)
5. [Конфигурация](#конфигурация)
6. [Описание API и Swagger](#описание-api-и-swagger)
7. [Примеры запросов](#примеры-запросов)
8. [Скриншоты](#скриншоты)

---

## Описание проекта

Проект представляет собой простое API для регистрации пользователей, аутентификации (login/logout) и выполнения платежей. Все защищённые эндпоинты требуют передачи JWT-токена в заголовке `Authorization`.

Ключевые особенности:

* Регистрация и хранение пользователей в PostgreSQL
* Аутентификация с помощью JWT
* Ограничение числа попыток входа (Login Limiter)
* Хранение и отзыв токенов в базе данных
* Документация API через Swagger UI

---

## Технологии

* Java 21
* Spring Boot 3
* Spring Security
* Spring Data JPA (Hibernate)
* PostgreSQL
* Bucket4j (rate limiting)
* Swagger / OpenAPI

---

## Требования

* JDK 21 или выше
* Maven 3.9+
* Docker или локально установленный PostgreSQL

---

## Установка и запуск

1. Клонировать репозиторий:

   ```bash
   git clone <repository-url>
   cd payment-api
   ```
2. Запустить PostgreSQL (например, через Docker Compose):

   ```bash
   docker-compose up -d
   ```
3. Собрать и запустить сервис:

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   Сервис будет доступен на `http://localhost:8080`.

---

## Конфигурация

Все параметры подключения к базе и настройки приложения описаны в `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5435/paydb
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
jwt:
  secret: super-secret-key-change-me
  ttl: PT30M

security:
  loginAttempts:
    max: 5
    lockMinutes: 15
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
payment:
  charge: 1.10

```

---

## Описание API и Swagger

Для удобства тестирования и изучения API интегрирован Swagger UI. После запуска сервиса документация доступна по адресу:

```
http://localhost:8080/swagger-ui.html
```

или

```
http://localhost:8080/swagger-ui/index.html
```

Swagger UI автоматически генерирует список эндпоинтов:

* `POST /api/auth/register` — регистрация нового пользователя
* `POST /api/auth/login` — вход (возвращает JWT токен)
* `POST /api/auth/logout` — выход (отзыв токена)
* `POST /api/payment` — создание платежа (требуется `Authorization: Bearer <token>`)

### Пример настройки авторизации в Swagger

1. Нажмите кнопку **Authorize** в правом верхнем углу.
2. Введите в поле значение:

   ```
   Bearer <ваш_JWT_токен>
   ```
3. Нажмите **Authorize** и **Close**.

После этого Swagger будет автоматически подставлять токен в заголовок `Authorization`.

---

## Примеры запросов

### Регистрация

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{ "username": "user1", "password": "pass123" }'
```

### Вход (login)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{ "username": "user1", "password": "pass123" }'
# Ответ: { "token": "eyJhbGci..." }
```

### Logout

```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H 'Authorization: Bearer eyJhbGci...'
```

### Платёж

```bash
curl -X POST http://localhost:8080/api/payment \
  -H 'Authorization: Bearer eyJhbGci...' \
  -H 'Content-Type: application/json' \
  -d '{ "amount": 50.00, "description": "Test payment" }'
```

---

## Скриншоты

### Swagger UI — авторизация токеном

![image](https://github.com/user-attachments/assets/df663ab2-c2ad-454e-a4e4-a8f5892ee618)
![image](https://github.com/user-attachments/assets/c23fdc3a-b6b0-482a-a71a-3fd7c8b187fe)

![image](https://github.com/user-attachments/assets/37d61b47-db33-498d-9f29-88f30e3ab859)



### Swagger UI — создание платежа

![image](https://github.com/user-attachments/assets/f89edc60-9320-4d35-b73a-bd6184bf16bb)


### После Logout токен становится недействительным и выводит 403 - Forbidden

![image](https://github.com/user-attachments/assets/2f23ff13-375a-433e-adcf-4dfbef9bdbe6)


