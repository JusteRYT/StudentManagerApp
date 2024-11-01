import db.DatabaseConfig;
import db.StudentDAO;
import model.Student;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class StudentDAOTest {
    private StudentDAO studentDAO;

    @BeforeEach
    void setUp() {
        studentDAO = new StudentDAO();
    }

    @Test
    void testAddStudent() {
        Student student = new Student();
        student.setFirstName("Иван");
        student.setLastName("Иванов");
        student.setPatronymic("Иванович");
        student.setBirthDate("21-08-2023");
        student.setGroupName("A1");
        student.setUniqueNumber("12345");

        assertDoesNotThrow(() -> studentDAO.addStudent(student));
    }

    @Test
    void testDeleteStudent() {
        Student student = new Student();
        student.setFirstName("Петр");
        student.setLastName("Петров");
        student.setPatronymic("Петрович");
        student.setBirthDate("01-01-2023");
        student.setGroupName("B2");
        student.setUniqueNumber("67890");

        try {
            studentDAO.addStudent(student);
            studentDAO.deleteStudent("67890");
            List<Student> students = studentDAO.getAllStudents();
            assertTrue(students.stream().noneMatch(s -> "67890".equals(s.getUniqueNumber())));
        } catch (SQLException e) {
            fail("SQL Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void testGetAllStudents() {
        try {
            List<Student> students = studentDAO.getAllStudents();
            assertNotNull(students, "Список студентов не должен быть null");
        } catch (SQLException e) {
            fail("SQL Exception occurred: " + e.getMessage());
        }
    }
}
