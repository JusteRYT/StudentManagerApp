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

        try {
            String method = exchange.getRequestMethod();
            response = switch (method) {
                case "GET" -> getStudents();
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
        System.out.println("Sending response: " + response); // Для отладки
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
        String jsonResponse = gson.toJson(students); // Преобразуем список студентов в JSON
        System.out.println("JSON Response: " + jsonResponse); // Для отладки
        return jsonResponse; // Возвращаем JSON
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
        String query = exchange.getRequestURI().getQuery();
        String uniqueNumber = null;

        // Извлекаем уникальный номер из параметров запроса
        if (query != null && query.startsWith("unique_number=")) {
            uniqueNumber = query.split("=")[1];
        }

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
        InputStream is = exchange.getRequestBody();
        String requestBody = new String(is.readAllBytes());
        Gson gson = new Gson();
        Student student = gson.fromJson(requestBody, Student.class);
        studentService.updateStudent(student);
        return "{\"message\": \"Student updated successfully\"}";
    }
}
