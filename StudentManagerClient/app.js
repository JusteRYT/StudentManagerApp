$(document).ready(function() {
    loadStudents();

    $('#add-student-form').submit(function(event) {
        event.preventDefault();
        const studentData = {
            firstName: $('#add-first-name').val() || null,
            lastName: $('#add-last-name').val() || null,
            patronymic: $('#add-patronymic').val() || null,
            birthDate: $('#add-birth-date').val() || null,
            groupName: $('#add-group-name').val() || null,
            uniqueNumber: $('#add-unique-number').val() || null
        };
        console.log("Данные для добавления студента:", studentData);
        addStudent(studentData);
    });

    $('#update-student-form').submit(function(event) {
        event.preventDefault();
        const studentData = {
            firstName: $('#update-first-name').val() || null,
            lastName: $('#update-last-name').val() || null,
            patronymic: $('#update-patronymic').val() || null,
            birthDate: $('#update-birth-date').val() || null,
            groupName: $('#update-group-name').val() || null
        };
        const uniqueNumber = $('#update-unique-number').val();
        console.log("Данные для обновления студента:", studentData);
        if (uniqueNumber) {
            updateStudent(uniqueNumber, studentData);
        } else {
            alert('Ошибка: уникальный номер не определен.');
        }
    });

    $('#close-popup').click(function() {
        $('#popup-overlay').hide();
    });
});

function loadStudents() {
    $.ajax({
        url: 'http://localhost:8080/api/students/',
        method: 'GET',
        success: function(data) {
            $('#student-list tbody').empty();
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

function editStudent(uniqueNumber) {
    $.ajax({
        url: `http://localhost:8080/api/students/${uniqueNumber}`,
        method: 'GET',
        success: function(student) {
            $('#display-unique-number').text(student.uniqueNumber);
            $('#update-unique-number').val(student.uniqueNumber);
            $('#current-first-name').text(student.firstName);
            $('#current-last-name').text(student.lastName);
            $('#current-patronymic').text(student.patronymic);
            $('#current-birth-date').text(student.birthDate);
            $('#current-group-name').text(student.groupName);

            $('#update-first-name').val('');
            $('#update-last-name').val('');
            $('#update-patronymic').val('');
            $('#update-birth-date').val('');
            $('#update-group-name').val('');

            $('#popup-overlay').show();
        },
        error: function(xhr) {
            alert('Ошибка загрузки информации о студенте: ' + xhr.responseText);
        }
    });
}

function addStudent(studentData) {
    $.ajax({
        url: 'http://localhost:8080/api/students/',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(studentData),
        success: function(response) {
            alert(response.message);
            loadStudents();
        },
        error: function(xhr) {
            alert('Ошибка добавления студента: ' + xhr.responseText);
        }
    });
}

function updateStudent(uniqueNumber, studentData) {
    $.ajax({
        url: `http://localhost:8080/api/students/${uniqueNumber}`,
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(studentData),
        success: function(response) {
            alert(response.message);
            loadStudents();
            $('#popup-overlay').hide();
        },
        error: function(xhr) {
            alert('Ошибка обновления студента: ' + xhr.responseText);
        }
    });
}

function deleteStudent(uniqueNumber) {
    $.ajax({
        url: `http://localhost:8080/api/students/${uniqueNumber}`,
        method: 'DELETE',
        success: function(response) {
            alert(response.message);
            loadStudents();
        },
        error: function(xhr) {
            alert('Ошибка удаления студента: ' + xhr.responseText);
        }
    });
}
