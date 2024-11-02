$(document).ready(function() {
    loadStudents(); // Загружаем студентов при загрузке страницы

    // Обработка отправки формы для добавления студента
    $('#add-student-form').submit(function(event) {
        event.preventDefault(); // Предотвращаем стандартное поведение формы
        addStudent();
    });
});

function loadStudents() {
    $.ajax({
        url: 'http://localhost:8080/api/students/', // Убедитесь, что этот URL соответствует вашему серверу
        method: 'GET',
        success: function(data) {
            $('#student-list').empty(); // Очищаем текущий список
            data.forEach(function(student) {
                $('#student-list').append(`<li>${student.firstName} ${student.lastName}</li>`);
            });
        },
        error: function(xhr) {
            alert('Ошибка загрузки студентов: ' + xhr.responseText);
        }
    });
}

function addStudent() {
    const studentData = {
        firstName: $('#first-name').val(),
        lastName: $('#last-name').val(),
        patronymic: $('#patronymic').val(),
        birthDate: $('#birth-date').val(),
        groupName: $('#group-name').val(),
        uniqueNumber: $('#unique-number').val()
    };

    console.log("Данные студента:", studentData); // Отладочная информация

    $.ajax({
        url: 'http://localhost:8080/api/students/',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(studentData),
        success: function(response) {
            alert(response.message);
            loadStudents(); // Обновляем список студентов
        },
        error: function(xhr) {
            alert('Ошибка добавления студента: ' + xhr.responseText);
        }
    });
}