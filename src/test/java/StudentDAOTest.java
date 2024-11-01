import db.StudentDAO;
import model.Student;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class StudentDAOTest {
    private StudentDAO studentDAO;
    private Random random;
    String Number1;
    String Number2;

    @BeforeEach
    void setUp() {
        studentDAO = new StudentDAO();
        random = new Random();
    }

    @Test
    void testAddStudent() {
        Number1 = String.valueOf(random.nextInt(1000000));
        Student student = new Student();
        student.setFirstName("Иван");
        student.setLastName("Иванов");
        student.setPatronymic("Иванович");
        student.setBirthDate("2001-02-02");
        student.setGroupName("A1");
        student.setUniqueNumber(Number1);

        assertDoesNotThrow(() -> studentDAO.addStudent(student));
    }

    @Test
    void testDeleteStudent() {
        Number2 = String.valueOf(random.nextInt(1000000));
        Student student = new Student();
        student.setFirstName("Петр");
        student.setLastName("Петров");
        student.setPatronymic("Петрович");
        student.setBirthDate("2001-02-02");
        student.setGroupName("B2");
        student.setUniqueNumber(Number2);

        try {
            studentDAO.addStudent(student);
            studentDAO.deleteStudent(String.valueOf(Number2));
            List<Student> students = studentDAO.getAllStudents();
            assertTrue(students.stream().noneMatch(s -> Number2.equals(s.getUniqueNumber())));
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
