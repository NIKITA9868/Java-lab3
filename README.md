# Java-1
Казино
Проект был создан с помощью Spring Boot. Был добавлен следующий функционал:
GET ендпоинт по адресу http://localhost:8080/api/game?id=0 с Query Parameters возвращает json
с полями id и name. id соответственно - id игры. name - название игры.
GET ендпоинт по адресу http://localhost:8080/api/user/Nikita c Path Parameters возвращает json
с полями name и balance. name - имя пользователя и параметр пути. balance в текущей версии
возвращает 0.