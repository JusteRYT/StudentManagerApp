package controller;

import com.google.gson.Gson;
import db.DatabaseConfig;
import model.Student;
import service.StudentService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
/**
 * Контроллер для управления данными о студентах.
 * Обрабатывает HTTP-запросы для получения, добавления и удаления студентов.
 */
@WebServlet("/students")
public class StudentController extends HttpServlet {
    public StudentService studentService;

    /**
     * Инициализация контроллера. Устанавливает соединение с базой данных.
     *
     * @throws ServletException если возникает ошибка соединения с базой данных.
     */
    @Override
    public void init() throws ServletException {
        try {
            Connection connection = DatabaseConfig.getConnection();
        } catch (SQLException e) {
            throw new ServletException("Database connection error", e);
        }
    }

    /**
     * Обрабатывает GET-запросы для получения списка студентов.
     *
     * @param request  HTTP-запрос.
     * @param response HTTP-ответ.
     * @throws ServletException если возникает ошибка обработки запроса.
     * @throws IOException      если возникает ошибка ввода-вывода.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        try {
            List<Student> students = studentService.getStudents();
            Gson gson = new Gson(); // Используем Gson для конвертации в JSON
            String json = gson.toJson(students);
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error retrieving students: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает POST-запросы для добавления нового студента.
     *
     * @param request  HTTP-запрос.
     * @param response HTTP-ответ.
     * @throws ServletException если возникает ошибка обработки запроса.
     * @throws IOException      если возникает ошибка ввода-вывода.
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        try {
            // Получение параметров из запроса
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String patronymic = request.getParameter("patronymic");
            String birthDate = request.getParameter("birthDate");
            String groupName = request.getParameter("groupNumber");

            //Создание объекта студента
            Student student = new Student();
            student.setFirstName(firstName);
            student.setLastName(lastName);
            student.setPatronymic(patronymic);
            student.setBirthDate(Date.valueOf(birthDate));
            student.setGroupName(groupName);

            //Добавление студента через сервис
            studentService.addStudent(student);
            response.setStatus(HttpServletResponse.SC_CREATED); // Статус 201 Created
            response.getWriter().write("{\"message\": \"Student added successfully\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error adding student " + e.getMessage());
        }
    }

    /**
     * Обрабатывает DELETE-запросы для удаления студента по уникальному номеру.
     *
     * @param request  HTTP-запрос.
     * @param response HTTP-ответ.
     * @throws ServletException если возникает ошибка обработки запроса.
     * @throws IOException      если возникает ошибка ввода-вывода.
     */
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        try {
            // Получение уникального номера студент из URL
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\": \"ID parameter is missing\"}");
                return;
            }

            int id = Integer.parseInt(idParam);
            studentService.deleteStudent(id);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\": \"Student deleted successfully\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error deleting student: " + e.getMessage());
        }catch (NumberFormatException e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("idParam error: " + e.getMessage());
        }
    }

    /**
     * Освобождает ресурсы, используемые контроллером.
     */
    @Override
    public void destroy(){

    }
}
