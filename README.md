# User Applications System REST API

Данный проект демонстрирует работу API для системы регистрации и обработки пользовательских заявок и реализует следующие функциональные возможности:
- Вход в систему, генерация JWT
- Проверка телефонного номера через API [DaData](https://dadata.ru/)
- Создание заявки
- Отправка заявки оператору на рассмотрение
- Вывод списка заявок с возможностью сортировки по дате создания и пагинацией
по 5 элементов, фильтрация по статусу
- Просмотр заявки
- Редактирование заявки
- Принятие заявки
- Отклонение заявки
- Вывод списка пользователей
- Назначение роли оператора

В системе предусмотрены роли:
- Пользователь (USER)
- Оператор (OPERATOR)
- Администратор (ADMIN)

## Стек
- Java 17
- Spring Boot 3.2
- Spring Data JPA
- Spring Security, JWT
- Spring Cloud
- PostgreSQL
- Liquibase
- Lombok
- Maven
- JUnit, Mockito


## Подготовка проекта
Изменение порта и данных для подключения к БД в файле application.propesties
```
# application.propesties

server.port=8086
spring.datasource.url=jdbc:postgresql://localhost:5432/user_applications
spring.datasource.username=postgres
spring.datasource.password=admin
```

## Получение токена аутентификации
Ниже есть данные для входа.
```
POST http://localhost:8086/api/auth/signin
Тело: {"phone":"+7911 111 11 11", "password":"123"}
```
После выполнения запроса в теле ответа скопируйте поле token и вставьте его в поле Bearer Token. Теперь можно выполнять методы, для которых необходима аутентификация.
### Пользователи
| Телефон | Роли             |
|---|------------------|
|+7 952 8521601| USER             |
|+7 952 8521602| OPERATOR         |
|+7 952 8521603| ADMIN            |
|+7 952 8521604| USER, OPERATOR   |
|+7 952 8521606| USER,ADMIN       |
|+7 952 8521605| OPERATOR, ADMIN  |
|+7 952 8521607| USER, OPERATOR, ADMIN |
У всех одинаковый **пароль**: 123.

## REST API

### Аутентификация *(доступ у всех)
```
Вход (генерация токенов)
POST /api/auth/signin
Тело: {"phone":"+7911 111 11 11", "password":"123"}
```
```
Выход (токен записан в блэклист, его больше нельзя использовать)
POST /api/auth/user_logout
```
### Работа с пользователями *(доступ только по токену, с ролью ADMIN)
```
Вывести список пользователей
GET /api/users
```
```
Добавить роль OPERATOR пользователю
POST /api/users/{phone}/role?change=OPERATOR
Обязательный параметр:
change, должен быть равен OPERATOR
```
### Работа с заявками *(доступ только по токену)
```
Создать новую заявку *(доступ с ролью USER)
POST /api/applications/new
Тело: {"status":"DRAFT","text":"любой текст"}
значения status: DRAFT, SENT
```
```
Изменить текст заявки *(доступны свои заявки со статусом DRAFT пользователю с ролью USER)
PUT /api/applications/{id}
Тело: {"text":"любой текст"}
```
```
Изменить статус заявки *(USER может изменить статусом заявки с DRAFT на SENT, OPERATOR - с SENT на ACCEPTED, REJECTED)
PUT /api/applications?change=SENT	
Параметр обязательный:
change - значения: DRAFT, SENT, ACCEPTED, REJECTED
```
```
Вывести заявку по номеру id *(для USER доступны все его заявки, для OPERATOR - заявки со статусом SENT, для ADMIN - заявки со статусами SENT, ACCEPTED, REJECTED)
GET /api/applications/{id}	
```
```
Вывести список заявок пользователя *(доступ с ролью USER)
GET /api/applications/my?page=0&sort=DESC (заявки пользователя с ролью USER)
Параметры необязательные:
page - номер страницы, по умолчанию 0
sort - сортировка, по умолчанию пустая, значения: ASC, DESC  	
```
```
Вывести список заявок с указанным статусом
GET /api/applications/?status=DRAFT&username=Анна&page=0&sort=ASC
Параметры обязательные:
status - статус заявки, значения: DRAFT, SENT, ACCEPTED, REJECTED *(есть ограничения по ролям)
username - имя/часть имени пользователя, создавшего заявку
Параметры необязательные:
page - номер страницы, по умолчанию 0
sort - сортировка, по умолчанию пустая, значения: ASC, DESC
```
