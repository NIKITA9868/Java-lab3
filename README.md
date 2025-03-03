# Java-1
# Game and User API

## Описание
Этот проект представляет собой Spring Boot API для управления играми и пользователями.

## Функциональность
- Получение названия игры по ID
- Добавление нового пользователя
- Получение списка всех пользователей
- Получение информации о пользователе по ID
- Удаление пользователя
- Обновление данных пользователя

## Требования
- Java 17+
- Maven 3+
- PostgreSQL (или другая база данных, поддерживаемая Spring Data JPA)

## API Эндпоинты

### Получение названия игры по ID
**GET** `/game/{id}`

**Пример запроса:**
```sh
curl -X GET http://localhost:8080/game/1
```
**Ответ:**
```json
{
  "name": "Game Name",
  "id": 1
}
```

### Добавление нового пользователя
**POST** `/user`

**Пример запроса:**
```sh
curl -X POST http://localhost:8080/user -H "Content-Type: application/json" -d '{"name": "John Doe"}'
```
**Ответ:**
```json
{
  "id": 1,
  "name": "John Doe"
}
```

### Получение списка всех пользователей
**GET** `/users`

**Пример запроса:**
```sh
curl -X GET http://localhost:8080/users
```

**Ответ:**
```json
[
  { "name": "User1", "id": 1 },
  { "name": "User2", "id": 2 }
]
```

### Получение информации о пользователе по ID
**GET** `/user?id={id}`

**Пример запроса:**
```sh
curl -X GET "http://localhost:8080/user?id=1"
```

**Ответ:**
```json
{
  "name": "User Name",
  "id": 1
}
```

### Удаление пользователя
**DELETE** `/user?id={id}`

**Пример запроса:**
```sh
curl -X DELETE "http://localhost:8080/user?id=1"
```
**Ответ:** HTTP 204 No Content

### Обновление данных пользователя
**PUT** `/user`

**Пример запроса:**
```sh
curl -X PUT http://localhost:8080/user -H "Content-Type: application/json" -d '{"id": 1, "name": "John Updated"}'
```
**Ответ:**
```json
{
  "id": 1,
  "name": "John Updated"
}
```