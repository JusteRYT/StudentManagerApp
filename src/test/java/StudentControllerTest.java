import com.google.gson.Gson;
import controller.StudentController;
import model.Student;
import service.StudentService;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class StudentControllerTest {
    private StudentController studentController;
    private StudentService studentService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PrintWriter writer;

    @Before
    public void setUp() throws Exception {
        studentService = mock(StudentService.class);
        studentController = new StudentController();
        studentController.studentService = studentService; // Установка мок-сервиса

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        writer = mock(PrintWriter.class);

        when(response.getWriter()).thenReturn(writer);
        studentController.init(); // Инициализация контроллера
    }

    @Test
    public void testDoGet() throws Exception {
        // Подготовка тестовых данных
        List<Student> students = new ArrayList<>();
        students.add(new Student(1, "Иван", "Иванов", "Иванович", Date.valueOf("2000-01-01"), "Группа 1"));
        when(studentService.getStudents()).thenReturn(students);

        // Выполнение теста
        studentController.doGet(request, response);

        // Проверка результата
        verify(writer).print(new Gson().toJson(students));
    }

    @Test
    public void testDoPost() throws Exception {
        // Подготовка параметров запроса
        when(request.getParameter("firstName")).thenReturn("Иван");
        when(request.getParameter("lastName")).thenReturn("Иванов");
        when(request.getParameter("patronymic")).thenReturn("Иванович");
        when(request.getParameter("birthDate")).thenReturn("2000-01-01");
        when(request.getParameter("groupNumber")).thenReturn("Группа 1");

        // Выполнение теста
        studentController.doPost(request, response);

        // Проверка вызова метода добавления студента
        verify(studentService).addStudent(any(Student.class));
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(writer).write("{\"message\": \"Student added successfully\"}");
    }

    @Test
    public void testDoDelete() throws Exception {
        // Подготовка параметров запроса
        when(request.getParameter("id")).thenReturn("1");

        // Выполнение теста
        studentController.doDelete(request, response);

        // Проверка вызова метода удаления студента
        verify(studentService).deleteStudent(1);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write("{\"message\": \"Student deleted successfully\"}");
    }
}