<h1 align="center">Тестовое задание для летней школы Яндекса</h1>

## Описание проекта

Реализовано задач: 5/5

Стек проекта: Spring Boot 2.7.0, Java 11, Spring Jpa, PostgreSQL, Lombok


## Сборка проекта
Для запуска проекта на своей машине, у вас должен быть установлен Docker и maven.

Чтобы установить maven введите команду: `sudo apt install maven`

- Склонируйте репозиторий в локальную директорию:
    - Зайдите в локальную директорию
    - Откройте CMD если вы на Windows
    - Введите команду `git clone https://github.com/Basnucaev/mega-market.git`
- Введите команду `mvn spring-boot:build-image -DskipTests` - команда запустит сборку проекта и создаст образ проекта на вашем компьютере пропустив этап тестирования, это нужно чтобы проект собрался без запущенного postgre контейнера
- После того как соборка проекта завершена и image создан, запустите команду `docker-compose up`, вы можете добавить параметр `-d` в конец команды, чтобы запустить проект в фоновом режиме

Если вы получили ошибку "got permission denied", запустите команду с приставкой `sudo`.

Если вы запускаете команду на Windows, вместо `mvn` используйте `mvnw`.

Проект будет запущен на порту 80, приятного использования!