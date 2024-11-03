package Handler;

import com.google.gson.Gson;
import model.Student;
import service.StudentService;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * StudentHandler обрабатывает HTTP-запросы для управления данными студентов.
 * Обрабатывает методы GET, POST, PUT, DELETE для работы с данными студентов,
 * включая получение списка, добавление, обновление и удаление студентов.
 */
public class StudentHandler implements HttpHandler {
    private final StudentService studentService = new StudentService();

    /**
     * Обрабатывает HTTP-запросы, направленные на /api/students.
     * @param exchange HttpExchange объект, содержащий запрос и ответ
     * @throws IOException в случае ошибки ввода-вывода
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response;
        int responseCode = 200; // По умолчанию - OK

        // Установка CORS заголовков
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Разрешает доступ с любого источника
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"); // Разрешенные методы
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type"); // Разрешенные заголовки

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // Отправляем статус 204 No Content
            return; // Завершаем обработку
        }

        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            response = switch (method) {
                case "GET" -> {
                    if (path.equals("/api/students/generateUniqueNumber")) {
                        yield generateUniqueNumber(exchange); // Вызов метода для генерации номера
                    } else if (path.matches("/api/students/\\w+")) {
                        yield getStudentByUniqueNumber(exchange); // Получаем студента по уникальному номеру
                    } else {
                        yield getStudents(); // Получаем всех студентов
                    }
                }
                case "POST" -> addStudent(exchange);
                case "DELETE" -> deleteStudent(exchange);
                case "PUT" -> updateStudent(exchange);
                default -> {
                    responseCode = 405; // Метод не разрешен
                    yield "Method not allowed";
                }
            };
        } catch (SQLException e) {
            responseCode = 500; // Внутренняя ошибка сервера
            response = "Error: " + e.getMessage();
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
        }

        // Отправка ответа
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(responseCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    /**
     * Обрабатывает запрос GET, возвращая список всех студентов в формате JSON.
     * @return JSON строка с данными всех студентов
     * @throws SQLException если произошла ошибка при обращении к базе данных
     */
    private String getStudents() throws SQLException {
        List<Student> students = studentService.getStudents(); // Получаем список студентов
        Gson gson = new Gson();
        return gson.toJson(students); // Возвращаем JSON
    }

    /**
     * Обрабатывает запрос POST для добавления нового студента.
     * @param exchange HttpExchange объект с запросом
     * @return JSON строка с сообщением о результатах добавления
     * @throws IOException если произошла ошибка чтения запроса
     * @throws SQLException если произошла ошибка при обращении к базе данных
     */
    private String addStudent(HttpExchange exchange) throws IOException, SQLException {
        InputStream is = exchange.getRequestBody();
        String requestBody = new String(is.readAllBytes());
        Gson gson = new Gson();
        Student student = gson.fromJson(requestBody, Student.class);
        studentService.addStudent(student);
        return "{\"message\": \"Student added successfully\"}";
    }

    /**
     * Обрабатывает запрос DELETE для удаления студента по уникальному номеру.
     * @param exchange HttpExchange объект с запросом
     * @return JSON строка с сообщением о результатах удаления
     * @throws SQLException если произошла ошибка при обращении к базе данных
     */
    private String deleteStudent(HttpExchange exchange) throws SQLException {
        // Получаем уникальный номер из пути запроса
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        String uniqueNumber = pathParts[pathParts.length - 1];

        if (uniqueNumber != null) {
            studentService.deleteStudent(uniqueNumber);
            return "{\"message\": \"Student deleted successfully\"}";
        } else {
            return "{\"error\": \"Unique number is required\"}";
        }
    }

    /**
     * Обрабатывает запрос PUT для обновления данных студента.
     * @param exchange HttpExchange объект с запросом
     * @return JSON строка с сообщением о результатах обновления
     * @throws IOException если произошла ошибка чтения запроса
     * @throws SQLException если произошла ошибка при обращении к базе данных
     */
    private String updateStudent(HttpExchange exchange) throws IOException, SQLException {
        // Получаем уникальный номер из пути запроса
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        String uniqueNumber = pathParts[pathParts.length - 1];
        System.out.println(uniqueNumber);
        // Проверяем, что уникальный номер является числом
        if (uniqueNumber == null || uniqueNumber.isEmpty()) {
            throw new IOException("Unique number is missing in the request URL.");
        }

        // Чтение тела запроса
        InputStream is = exchange.getRequestBody();
        String requestBody = new String(is.readAllBytes());
        Gson gson = new Gson();

        // Преобразуем JSON в объект Student
        Student studentData = gson.fromJson(requestBody, Student.class);
        studentData.setUniqueNumber(uniqueNumber); // Устанавливаем уникальный номер

        // Обновляем студента
        studentService.updateStudent(studentData);

        return "{\"message\": \"Student updated successfully\"}";
    }

    /**
     * Обрабатывает запрос GET для получения данных о студенте по уникальному номеру.
     *
     * @param exchange HttpExchange объект, содержащий запрос и ответ.
     * @return JSON строка с данными студента, если найден, иначе отправляет статус 404.
     * @throws IOException если произошла ошибка ввода-вывода.
     * @throws SQLException если произошла ошибка при обращении к базе данных.
     */
    private String getStudentByUniqueNumber(HttpExchange exchange) throws IOException, SQLException{
        String uniqueNumber = exchange.getRequestURI().getPath().split("/")[3];
        Student student = studentService.getStudentsByUniqueNumber(uniqueNumber);

        if(student != null){
            Gson gson = new Gson();
            return gson.toJson(student);
        } else {
            exchange.sendResponseHeaders(404, -1);
            return null;
        }
    }

    /**
     * Генерирует уникальный номер для нового студента и проверяет его уникальность.
     *
     * @param exchange HttpExchange объект с запросом
     * @return JSON строка с уникальным номером
     * @throws IOException если произошла ошибка при ответе
     * @throws SQLException если произошла ошибка при обращении к базе данных
     */
    private String generateUniqueNumber(HttpExchange exchange) throws IOException, SQLException {
        String uniqueNumber;
        do {
            uniqueNumber = String.valueOf((int) (Math.random() * 1_000_000)); // Генерация случайного номера
        } while (studentService.isUniqueNumberExists(uniqueNumber)); // Проверка уникальности

        return "{\"uniqueNumber\": \"" + uniqueNumber + "\"}";
    }
}
