# Demo Project API
л
## Описание
Этот проект представляет собой REST API для управления пользователями и играми. API предоставляет CRUD-операции для работы с сущностями `User` и `Game`.

### Требования
- Java 17+
- Spring Boot
- Maven
- База данных (например, PostgreSQL или H2 для локальной разработки)

## API Документация

### UserController
#### Получить пользователя по имени
```http
GET /user/{name}
```
**Ответы:**
- `200 OK` - успешный ответ с данными пользователя
- `400 BAD REQUEST` - некорректный запрос
- `404 NOT FOUND` - пользователь не найден

#### Получить пользователя по ID
```http
GET /user?id={id}
```
**Ответы:**
- `200 OK` - успешный ответ с данными пользователя
- `400 BAD REQUEST` - некорректный ID
- `404 NOT FOUND` - пользователь не найден

#### Получить всех пользователей
```http
GET /user/all
```
**Ответ:**
- `200 OK` - список пользователей

#### Добавить пользователя
```http
POST /user
Content-Type: application/json
{
  "name": "John Doe",
  "age": 30
}
```
**Ответ:**
- `201 CREATED` - пользователь успешно создан
- `400 BAD REQUEST` - некорректные данные

#### Удалить пользователя
```http
DELETE /user?id={id}
```
**Ответ:**
- `204 NO CONTENT` - пользователь удалён
- `404 NOT FOUND` - пользователь не найден

#### Обновить пользователя
```http
PUT /user
Content-Type: application/json
{
  "id": 1,
  "name": "John Updated",
  "age": 35
}
```
**Ответ:**
- `200 OK` - пользователь обновлён
- `404 NOT FOUND` - пользователь не найден

---
### GameController
#### Получить игру по ID
```http
GET /bet?id={id}
```
**Ответы:**
- `200 OK` - успешный ответ с данными игры
- `400 BAD REQUEST` - некорректный ID
- `404 NOT FOUND` - игра не найдена

#### Получить все игры
```http
GET /bet/all
```
**Ответ:**
- `200 OK` - список игр

#### Добавить игру
```http
POST /bet
Content-Type: application/json
{
  "name": "Chess",
  "id": 101
}
```
**Ответ:**
- `201 CREATED` - игра успешно создана
- `400 BAD REQUEST` - некорректные данные

#### Удалить игру
```http
DELETE /bet?id={id}
```
**Ответ:**
- `204 NO CONTENT` - игра удалена
- `404 NOT FOUND` - игра не найдена

#### Обновить игру
```http
PUT /bet
Content-Type: application/json
{
  "id": 101,
  "name": "Updated Chess"
}
```
**Ответ:**
- `200 OK` - игра обновлена
- `404 NOT FOUND` - игра не найдена


