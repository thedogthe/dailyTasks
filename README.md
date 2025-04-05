# Daily Tasks REST API

Простое REST API для управления списком задач с возможностью фильтрации по периодам и статусу выполнения.

## 📋 Возможности

- ✅ Просмотр задач на сегодня/неделю/месяц
- ✅ Фильтрация по статусу выполнения
- ✅ Создание, изменение и удаление задач
- ✅ Отметка задач как выполненных/невыполненных
- ✅ Простая и понятная REST архитектура

## 🚀 Быстрый старт

### Требования

- Java 21+
- Maven 7.x+
- PostgreSQL 17+

### Установка и запуск

Клонируйте репозиторий:

```bash
git clone https://github.com/thedogthe/dailyTasks.git
cd dailyTasks
mvn clean package
mvn spring-boot:run
```

## 🛠 Эндпоинты

```table
| Метод | Путь               | Описание                          |
|-------|--------------------|-----------------------------------|
| GET   | /tasks             | Получить все задачи               |
| GET   | /today             | Задачи на сегодня                 |
| GET   | /week              | Задачи на неделю                  |
| GET   | /month             | Задачи на месяц                   |
| GET   | /complete          | Выполненные задачи                |
| GET   | /pending           | Невыполненные задачи              |
| GET   | /{id}              | Получить задачу по ID             |
| POST  | /                  | Создать новую задачу              |
| PUT   | /{id}              | Обновить задачу                   |
| PATCH | /{id}/completion   | Отметить задачу как выполненную   |
| PATCH | /{id}/uncomplete   | Отметить задачу как невыполненную |
| DELETE| /{id}              | Удалить задачу                    |
```

## 📄 Примеры запросов

### Создание задачи

```bash
curl --location 'http://localhost:8080/tasks' \
--header 'Content-Type: application/json' \
--header 'Content-Type: application/json' \
--data '{
    "title": "Купить молоко",
    "description": "2.5%",
    "dueDate": "2025-04-12",
    "complete" : false
}'
```

### Получение задач на неделю

```bash
curl --location 'http://localhost:8080/tasks/week'
```

### Отметка задачи как выполненной

```bash
curl --location --request PATCH 'http://localhost:8080/tasks/3/completion'
```

## 🛠 Технологии

Java 21 -  Язык разработки
Spring Boot 3 - Фреймворк для создания приложения
Maven - Система сборки
PostgreSQL - База данных
Lombok - Для сокращения boilerplate кода
Spring Data JPA - Для работы с данными

## 🧪 Тестирование

Для запуска тестов:

```bash
mvn test
```

Тесты включают:

Unit тесты сервисов
Интеграционные тесты контроллеров
Тесты валидации

## 📦 Структура проекта

```src/
├── main/
│   ├── java/com/the/dailytasks/
│   │   ├── controller/    # REST контроллеры
│   │   ├── model/         # Сущности
│   │   ├── repository/    # Репозитории данных
│   │   ├── service/       # Бизнес-логика
│   │   └── exception/     # Обработка ошибок
│   └── resources/         # Конфигурации
└── test/                  # Тесты
```

## ✅ TODO

Write and fix test 

![img.png](docs/img.png)
