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

public class StudentHandler implements HttpHandler {
    private final StudentService studentService = new StudentService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response;
        int responseCode = 200; // По умолчанию - OK

        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    response = getStudents();
                    break;
                case "POST":
                    response = addStudent(exchange);
                    break;
                case "DELETE":
                    response = deleteStudent(exchange);
                    break;
                default:
                    responseCode = 405; // Метод не разрешен
                    response = "Method not allowed";
                    break;
            }
        } catch (SQLException e) {
            responseCode = 500; // Внутренняя ошибка сервера
            response = "Error: " + e.getMessage();
        }

        // Отправка ответа
        exchange.sendResponseHeaders(responseCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String getStudents() throws SQLException {
        List<Student> students = studentService.getStudents();
        Gson gson = new Gson();
        return gson.toJson(students);
    }

    private String addStudent(HttpExchange exchange) throws IOException, SQLException {
        InputStream is = exchange.getRequestBody();
        String requestBody = new String(is.readAllBytes());
        Gson gson = new Gson();
        Student student = gson.fromJson(requestBody, Student.class);
        studentService.addStudent(student);
        return "{\"message\": \"Student added successfully\"}";
    }

    private String deleteStudent(HttpExchange exchange) throws IOException, SQLException {
        String query = exchange.getRequestURI().getQuery();
        int id = Integer.parseInt(query.split("=")[1]);
        studentService.deleteStudent(id);
        return "{\"message\": \"Student deleted successfully\"}";
    }
}
