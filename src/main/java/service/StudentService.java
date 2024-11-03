package service;

import db.StudentDAO;
import model.Student;

import java.sql.SQLException;
import java.util.List;
/**
 * StudentService - класс, который управляет операциями с данными студентов.
 * Он выполняет бизнес-логику и взаимодействует с DAO для работы с базой данных.
 * <p>
 * Responsibilities:
 * - Получение списка студентов
 * - Добавление нового студента
 * - Удаление студента по уникальному номеру
 * - Обновление информации о студенте
 * - Получение студента по уникальному номеру
 */
public class StudentService {
    private final StudentDAO studentDAO; // DAO для работы с данными студентов

    public StudentService() {
        this.studentDAO = new StudentDAO(); // Инициализация DAO
    }

    /**
     * Получает список всех студентов из базы данных.
     *
     * @return список студентов
     * @throws SQLException если произошла ошибка доступа к базе данных
     */
    public List<Student> getStudents() throws SQLException {
        return studentDAO.getAllStudents();
    }

    /**
     * Добавляет нового студента в базу данных.
     *
     * @param student объект студента, который нужно добавить
     * @throws SQLException если произошла ошибка доступа к базе данных
     */
    public void addStudent(Student student) throws SQLException {
        studentDAO.addStudent(student);
    }

    /**
     * Удаляет студента из базы данных по уникальному номеру.
     *
     * @param unique_number уникальный номер студента, которого нужно удалить
     * @throws SQLException если произошла ошибка доступа к базе данных
     */
    public void deleteStudent(String unique_number) throws SQLException {
        studentDAO.deleteStudent(unique_number);
    }

    /**
     * Обновление информации студента
     *
     * @param student обновленная информация для студента
     * @throws SQLException если произошла ошибка доступа к базе данных
     */
    public void updateStudent(Student student) throws SQLException{
        studentDAO.updateStudent(student);
    }

    /**
     * Получение информации о студенте по уникальному номеру.
     *
     * @param uniqueNumber уникальный идентификатор для студента.
     * @return объект студента, если найден, иначе null.
     * @throws SQLException если произошла ошибка при обращении к базе данных.
     */
    public Student getStudentsByUniqueNumber(String uniqueNumber) throws SQLException {
       return studentDAO.getStudentByUnique(uniqueNumber);
    }

    /**
     * Проверяет, существует ли уникальный номер студента в базе данных.
     *
     * @param uniqueNumber уникальный номер студента для проверки
     * @return true, если уникальный номер существует, иначе false
     * @throws SQLException если произошла ошибка доступа к базе данных
     */
    public boolean isUniqueNumberExists(String uniqueNumber) throws SQLException{
        return studentDAO.isUniqueNumberExists(uniqueNumber);
    }
}
