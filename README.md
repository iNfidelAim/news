# news
Parser CSV and Data Base

Spring-boot приложение для отображения новостных данных.

WEB сервис
Необходимо реализовать функциональность REST сервиса в котом будет реализовано:

API для получения данных из БД:
Обрабатываемых источниках
Тематиках новостей
Новостях (всех, всех по источнику, всех по тематике) с функционалом пагинации
Безобасность через API токен клиента с правами на все методы API (без регистрации)
Реализовать статистическую выгрузку, запускающуюся по cron-рассписанию.
Используя многопоточность, для каждого источника составить csv формата Тема,Количество новостей. Файл называть названием источника. (Не реализовано)
Миграции для базы данных
Написать docker-compose в котором будет запускаться postgresql и приложение

----------------------------------------------------------

Изменил тестовые данные, добавил id, и проверку на published
-----------------------------------------------------------


----------------------------------------------------------

localhost:8080/api/csv/download/{fileName:.+} GET загрузить все сохраненные данные из базы данных 
localhost:8080/api/csv/upload POST для загрузки в бд
localhost:8080/api/csv/tutorials GET JSON формат

Postman не будет выполнять запросы пока юзер не залогинится.


application properties:

spring.datasource.url= jdbc:postgresql://localhost:5432/news
spring.datasource.username=****
spring.datasource.password=****
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.show_sql=true

#Hibernate ddl auto (create, create-drop, validate, update)

spring.jpa.hibernate.ddl-auto= update

#Configure the file size to be uploaded

spring.servlet.multipart.max-file-size=2MB
spring.servlet.multipart.max-request-size=2MB



docker-compose.yml :

version: '20.10.21'
services:
  postgres:
    image: postgres:9.6
    ports:
      - 5432:5432
    restart: always
  app:
    image: app
    depends_on:
      - postgres
    ports:
      - 8080:8080
    restart: always
