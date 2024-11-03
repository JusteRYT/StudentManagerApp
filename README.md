# 📚 StudentManagerApp

**StudentManagerApp** — это веб-приложение для управления данными о студентах, позволяющее добавлять, удалять, обновлять и просматривать информацию о студентах. Приложение использует REST API для взаимодействия между клиентской и серверной частями.

## 📖 Содержание

- [Основные компоненты](#-основные-компоненты)
- [Технологии](#-технологии)
- [Логика app.js](#-логика-appjs)
- [Скриншоты](#-скриншоты)
- [Скачивание и установка](#-скачивание-и-установка)
- [Заключение](#-заключение)

## 🚀 Основные компоненты

- **Клиентская часть (Frontend)**
  - Разработана с использованием **HTML**, **CSS** и **JavaScript**.
  - Отвечает за отображение данных студентов и взаимодействие с пользователем.

- **Серверная часть (Backend)**
  - Реализована на **Java** с использованием **HttpServer**.
  - Обрабатывает HTTP-запросы, взаимодействует с базой данных и выполняет бизнес-логику.

- **База данных**
  - Используется **MySQL** для хранения данных студентов.
  - Данные включают уникальный номер, имя, фамилию, отчество, дату рождения и группу.

## 🛠️ Технологии

- **Java** — для реализации серверной части.
- **HTML/CSS/JavaScript** — для клиентской части.
- **MySQL** — для хранения данных о студентах.
- **Gson** — для сериализации и десериализации JSON-данных.

## 🔍 Логика `app.js`

Файл `app.js` реализует функциональность клиентской части, включая:

- Обработку событий для кнопок и форм.
- Отправку HTTP-запросов на сервер (GET, POST, PUT, DELETE).
- Обновление интерфейса в зависимости от ответов сервера.
- Генерацию уникального номера для новых студентов с помощью запроса на сервер.

```javascript
// Пример логики для отправки POST-запроса на добавление студента
function addStudent(studentData) {
    fetch('http://localhost:8080/api/students/', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(studentData)
    })
    .then(response => response.json())
    .then(data => {
        console.log(data);
        // Обновление UI после добавления студента
    })
    .catch(error => console.error('Error:', error));
}
```
## 🖼️ Скриншоты

Вот несколько скриншотов интерфейса приложения:

### Главная страница
Главная страница приложения, где отображается список студентов.
![Главная страница](https://github.com/user-attachments/assets/ef026585-2eca-4860-b121-39599451b7bb)

### Форма добавления студента
Форма для добавления нового студента в базу данных.
![Форма добавления студента](https://github.com/user-attachments/assets/f3ed7d63-706e-4883-b983-745289171930)

### Таблица с информацией о студентах
Страница с информацией о выбранном студенте.
![Таблица с информацией о студентах](https://github.com/user-attachments/assets/2c4c4c5f-68d8-4f56-82f0-994ef5b45202)

## 📦 Скачивание и установка

Все файлы можно скачать в релизе **v 1.0.0**. В нем есть `.bat` файл, который запускает сервер, и `index.html` с страницей.

Запустите сервер с помощью `start.bat`, который содержит следующий код:

```bash
java -jar StudentManagerApp.jar
```


## 🎉 Заключение

StudentManagerApp — это практическое приложение для изучения технологий веб-разработки и работы с базами данных. Приложение наглядно демонстрирует основные принципы создания REST API и взаимодействия клиент-сервер.
