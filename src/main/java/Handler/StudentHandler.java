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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * StudentHandler обрабатывает HTTP-запросы для управления данными студентов.
 * Обрабатывает методы GET, POST, PUT, DELETE для работы с данными студентов,
 * включая получение списка, добавление, обновление и удаление студентов.
 */
public class StudentHandler implements HttpHandler {
    private final StudentService studentService = new StudentService();
    private static final Logger logger = Logger.getLogger(StudentHandler.class.getName());

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
        setupCORS(exchange);

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            response = switch (method) {
                case "GET" -> handleGetRequest(path, exchange);
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
            logger.log(Level.SEVERE, "Database error: ", e);
        } catch (IOException e) {
            responseCode = 400; // Плохой запрос
            response = "Error: " + e.getMessage();
            logger.log(Level.SEVERE, "IO error: ", e);
        }

        sendResponse(exchange, responseCode, response);
    }

    /**
     * Устанавливает CORS заголовки для разрешения запросов с других источников.
     *
     * @param exchange Объект HttpExchange, представляющий HTTP-запрос.
     */
    private void setupCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    /**
     * Отправляет ответ клиенту с заданным кодом состояния и сообщением.
     *
     * @param exchange    Объект HttpExchange, представляющий HTTP-запрос.
     * @param responseCode Код состояния ответа.
     * @param response     Сообщение ответа в формате JSON.
     * @throws IOException В случае ошибки ввода-вывода.
     */
    private void sendResponse(HttpExchange exchange, int responseCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(responseCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    /**
     * Обрабатывает GET-запросы в зависимости от URL.
     *
     * @param path    Путь запроса.
     * @param exchange Объект HttpExchange, представляющий HTTP-запрос.
     * @return Ответ в формате JSON.
     * @throws SQLException В случае ошибки базы данных.
     * @throws IOException  В случае ошибки ввода-вывода.
     */
    private String handleGetRequest(String path, HttpExchange exchange) throws SQLException, IOException {
        return switch (path) {
            case "/api/students/generateUniqueNumber" -> generateUniqueNumber(exchange);
            case "/api/students/" -> getStudents();
            default -> getStudentByUniqueNumber(exchange);
        };
    }

    /**
     * Получает список всех студентов из базы данных.
     *
     * @return Список студентов в формате JSON.
     * @throws SQLException В случае ошибки базы данных.
     */
    private String getStudents() throws SQLException {
        List<Student> students = studentService.getStudents();
        return new Gson().toJson(students);
    }

    /**
     * Добавляет нового студента в базу данных.
     *
     * @param exchange Объект HttpExchange, представляющий HTTP-запрос.
     * @return Сообщение об успешном добавлении студента в формате JSON.
     * @throws IOException  В случае ошибки ввода-вывода.
     * @throws SQLException В случае ошибки базы данных.
     */
    private String addStudent(HttpExchange exchange) throws IOException, SQLException {
        Student student = parseStudentFromRequest(exchange);
        studentService.addStudent(student);
        return "{\"message\": \"Student added successfully\"}";
    }

    /**
     * Парсит объект студента из тела запроса.
     *
     * @param exchange Объект HttpExchange, представляющий HTTP-запрос.
     * @return Объект Student.
     * @throws IOException В случае ошибки ввода-вывода.
     */
    private Student parseStudentFromRequest(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String requestBody = new String(is.readAllBytes());
        return new Gson().fromJson(requestBody, Student.class);
    }

    /**
     * Удаляет студента по уникальному номеру.
     *
     * @param exchange Объект HttpExchange, представляющий HTTP-запрос.
     * @return Сообщение об успешном удалении студента в формате JSON.
     * @throws SQLException В случае ошибки базы данных.
     */
    private String deleteStudent(HttpExchange exchange) throws SQLException {
        String uniqueNumber = extractUniqueNumberFromPath(exchange);
        if (uniqueNumber != null) {
            studentService.deleteStudent(uniqueNumber);
            return "{\"message\": \"Student deleted successfully\"}";
        } else {
            return "{\"error\": \"Unique number is required\"}";
        }
    }

    /**
     * Обновляет информацию о студенте.
     *
     * @param exchange Объект HttpExchange, представляющий HTTP-запрос.
     * @return Сообщение об успешном обновлении студента в формате JSON.
     * @throws IOException  В случае ошибки ввода-вывода.
     * @throws SQLException В случае ошибки базы данных.
     */
    private String updateStudent(HttpExchange exchange) throws IOException, SQLException {
        String uniqueNumber = extractUniqueNumberFromPath(exchange);
        if (uniqueNumber == null || uniqueNumber.isEmpty()) {
            throw new IOException("Unique number is missing in the request URL.");
        }

        Student studentData = parseStudentFromRequest(exchange);
        studentData.setUniqueNumber(uniqueNumber);
        studentService.updateStudent(studentData);
        return "{\"message\": \"Student updated successfully\"}";
    }

    /**
     * Извлекает уникальный номер студента из пути запроса.
     *
     * @param exchange Объект HttpExchange, представляющий HTTP-запрос.
     * @return Уникальный номер студента.
     */
    private String extractUniqueNumberFromPath(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        return pathParts[pathParts.length - 1];
    }

    /**
     * Получает информацию о студенте по уникальному номеру.
     *
     * @param exchange Объект HttpExchange, представляющий HTTP-запрос.
     * @return Информация о студенте в формате JSON.
     * @throws IOException  В случае ошибки ввода-вывода.
     * @throws SQLException В случае ошибки базы данных.
     */
    private String getStudentByUniqueNumber(HttpExchange exchange) throws IOException, SQLException {
        String uniqueNumber = extractUniqueNumberFromPath(exchange);
        Student student = studentService.getStudentsByUniqueNumber(uniqueNumber);

        if (student != null) {
            return new Gson().toJson(student);
        } else {
            exchange.sendResponseHeaders(404, -1);
            return "{\"error\": \"Student not found\"}";
        }
    }

    /**
     * Генерирует уникальный номер для студента.
     *
     * @param exchange Объект HttpExchange, представляющий HTTP-запрос.
     * @return Уникальный номер в формате JSON.
     * @throws IOException  В случае ошибки ввода-вывода.
     * @throws SQLException В случае ошибки базы данных.
     */
    private String generateUniqueNumber(HttpExchange exchange) throws IOException, SQLException {
        String uniqueNumber;
        do {
            uniqueNumber = String.valueOf((int) (Math.random() * 1_000_000));
        } while (studentService.isUniqueNumberExists(uniqueNumber));

        return "{\"uniqueNumber\": \"" + uniqueNumber + "\"}";
    }
}
