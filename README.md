# HashTranslator. Приложение для расшифровки хешей

## Описание

Приложение должно состоять из двух взаимодействующих друг с другом сервисов (сервис авторизации и
сервис расшифровки хешей).
Сервис авторизации отвечает непосредственно за авторизацию и предоставляет API для создания (
удаления) пользователей.
Создавать (удалять) пользователей должен заранее преднастроенный администратор сервиса и передавать
данные для авторизации пользователям.
Информация о пользователе должна содержать email и password.
Сервис расшифровки хэшей предоставляет API для отправки заявки (application) на расшифровку MD5
хешей, взаимодействует с сервисом авторизации и возвращает уникальный id заявки.
Заявка включает в себя массив из 1+ хешей.
Пользователь должен иметь возможность отправить запрос (указав идентификатор заявки) и получить
результат обработки заявки.
HashTranslator сам не выполняет расшифровку хешей, а делегирует эту операцию публичным бесплатным
сервисам.

## Пример
Авторизация осуществляется с использованием JWT-cookie(httpOnly).
### REST API для обработки заявок

```http applications request
POST /api/v1/applications
Content-Type: application/json
{
  "hashes": [
    "c4ca4238a0b923820dcc509a6f75849b",
    "c81e728d9d4c2f636f067f89cc14862c",
    "eccbc87e4b5ce2fe28308fd9f2a7baf3",
    "AB4F63F9AC65152575886860DDE480A1",
    "f5a7924e621e84c9280a9a27e1bcb7f6"
  ]
}
```

```http applications result request
GET /api/v1/applications/{id}
```

При получении результата обработки заявки, в ответе возвращается json со структурой:

```
{
  "successHashes": [
    {
      "hash": "3"
    },
    {
      "hash": "1"
    },
    {
      "hash": "2"
    }
  ],
  "failedHashes": [
    {
      "hash": "ab4f63f9ac65152575886860nng480a1"
    }
  ]
}
```

### Расшифровка хешей

Для расшифровки хешей используется API сервиса https://md5.opiums.eu.

### Docker Instruction

For generated gradle build folder

````
gradle clean build jar
````

Build and Run Docker Images

````
docker-compose up
````

Rebuild Docker Images

````
docker-compose build
````

Local:

+ auth: http://localhost:8080/swagger-ui/index.html
+ hash-translator: http://localhost:8085/swagger-ui/index.html

## Contact

[![LinkedIn][linkedin-shield]][linkedin-url]

<!-- MARKDOWN LINKS & IMAGES -->

[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555

[linkedin-url]: https://www.linkedin.com/in/kkarpekina