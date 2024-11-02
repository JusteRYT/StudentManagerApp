// Загружаем студентов при загрузке страницы
$(document).ready(function() {
    loadStudents();

    // Обработка отправки формы для добавления студента
    $('#add-student-form').submit(function(event) {
        event.preventDefault(); // Предотвращаем стандартное поведение формы
        addStudent();
    });

    // Обработка отправки формы для обновления студента
    $('#update-student-form').submit(function(event) {
        event.preventDefault(); // Предотвращаем стандартное поведение формы
        const studentData = {
            uniqueNumber: $('#update-unique-number').val(),
            firstName: $('#update-first-name').val(),
            lastName: $('#update-last-name').val(),
            patronymic: $('#update-patronymic').val(),
            birthDate: $('#update-birth-date').val(),
            groupName: $('#update-group-name').val()
        };
        updateStudent(studentData); // Вызываем функцию обновления
    });

    // Закрытие всплывающего окна
    $('#close-popup').click(function() {
        $('#popup-overlay').hide(); // Скрываем оверлей
    });
});

// Функция для загрузки студентов
function loadStudents() {
    $.ajax({
        url: 'http://localhost:8080/api/students/', // URL для запроса
        method: 'GET',
        success: function(data) {
            $('#student-list tbody').empty(); // Очищаем текущий список
            data.forEach(function(student) {
                $('#student-list tbody').append(`
                    <tr>
                        <td>${student.firstName}</td>
                        <td>${student.lastName}</td>
                        <td>${student.patronymic}</td>
                        <td>${student.birthDate}</td>
                        <td>${student.groupName}</td>
                        <td>${student.uniqueNumber}</td>
                        <td>
                            <button onclick="editStudent('${student.uniqueNumber}')">Редактировать</button>
                            <button onclick="deleteStudent('${student.uniqueNumber}')">Удалить</button>
                        </td>
                    </tr>
                `);
            });
        },
        error: function(xhr) {
            alert('Ошибка загрузки студентов: ' + xhr.responseText);
        }
    });
}

// Функция для редактирования студента
function editStudent(uniqueNumber) {
    // Здесь необходимо получить данные студента по уникальному номеру
    $.ajax({
        url: `http://localhost:8080/api/students/${uniqueNumber}`, // URL для получения студента
        method: 'GET',
        success: function(student) {
            // Заполняем форму данными студента
            $('#update-unique-number').val(student.uniqueNumber);
            $('#update-first-name').val(student.firstName);
            $('#update-last-name').val(student.lastName);
            $('#update-patronymic').val(student.patronymic);
            $('#update-birth-date').val(student.birthDate);
            $('#update-group-name').val(student.groupName);
            $('#popup-overlay').show(); // Показываем форму как всплывающее окно
        },
        error: function(xhr) {
            alert('Ошибка загрузки информации о студенте: ' + xhr.responseText);
        }
    });
}

// Функция для обновления студента
function updateStudent(studentData) {
    $.ajax({
        url: 'http://localhost:8080/api/students/',
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(studentData),
        success: function(response) {
            alert(response.message);
            loadStudents(); // Обновляем список студентов
            $('#popup-overlay').hide(); // Скрываем оверлей после обновления
        },
        error: function(xhr) {
            alert('Ошибка обновления студента: ' + xhr.responseText);
        }
    });
}

// Функция для добавления студента
function addStudent() {
    const studentData = {
        uniqueNumber: $('#add-unique-number').val(),
        firstName: $('#add-first-name').val(),
        lastName: $('#add-last-name').val(),
        patronymic: $('#add-patronymic').val(),
        birthDate: $('#add-birth-date').val(),
        groupName: $('#add-group-name').val()
    };

    $.ajax({
        url: 'http://localhost:8080/api/students/',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(studentData),
        success: function(response) {
            alert(response.message);
            loadStudents(); // Обновляем список студентов
            $('#add-student-form')[0].reset(); // Сбрасываем форму
        },
        error: function(xhr) {
            alert('Ошибка добавления студента: ' + xhr.responseText);
        }
    });
}

// Функция для удаления студента
function deleteStudent(uniqueNumber) {
    if (confirm('Вы уверены, что хотите удалить этого студента?')) {
        $.ajax({
            url: `http://localhost:8080/api/students/${uniqueNumber}`,
            method: 'DELETE',
            success: function(response) {
                alert(response.message);
                loadStudents(); // Обновляем список студентов
            },
            error: function(xhr) {
                alert('Ошибка удаления студента: ' + xhr.responseText);
            }
        });
    }
}
