package db;

import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для выполнения операций с базой данных для студентов.
 */
public class StudentDAO {


    public boolean isUniqueNumberExists(String uniqueNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE unique_number = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uniqueNumber);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Если число найденных записей больше 0, значит номер не уникален
            }
        }
        return false;
    }

    /**
     * Добавляет нового студента в базу данных.
     *
     * @param student объект Student, который нужно добавить
     * @throws SQLException если не удалось добавить студента
     */
    public void addStudent(Student student) throws SQLException {
        if (isUniqueNumberExists(student.getUniqueNumber())) {
            throw new SQLException("Unique number already exists.");
        }
        String sql = "INSERT INTO students (first_name, last_name, patronymic, birth_date, group_name, unique_number) " +
                "VALUES (?,?,?,?,?,?)";
        try (Connection connect = DatabaseConfig.getConnection();
             PreparedStatement statement = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getLastName());
            statement.setString(3, student.getPatronymic());
            statement.setDate(4, new java.sql.Date(student.getBirthDate().getTime()));
            statement.setString(5, student.getGroupName());
            statement.setString(6, student.getUniqueNumber());
            int affectedRows = statement.executeUpdate();
            // Получаем сгенерированные ключи
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        student.setId(generatedKeys.getInt(1)); // Устанавливаем сгенерированный ID в объекте Student
                    } else {
                        throw new SQLException("Creating student failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Обработка ошибок
        }
    }


    /**
     * Удаляет студента по уникальному номеру.
     *
     * @param uniqueNumber уникальный номер студента
     * @throws SQLException если не удалось удалить студента
     */
    public void deleteStudent(String uniqueNumber) throws SQLException {
        String sql = "DELETE FROM students WHERE unique_number = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uniqueNumber);
            int rowsAffected = statement.executeUpdate();
            if(rowsAffected == 0){
                throw new SQLException("No student found with unique number: " + uniqueNumber);
            }
        }
    }

    /**
     * Возвращает список всех студентов из базы данных.
     *
     * @return список студентов
     * @throws SQLException если не удалось получить список
     */
    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection connection = DatabaseConfig.getConnection();
             Statement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setPatronymic(rs.getString("patronymic"));
                student.setBirthDate(rs.getDate("birth_date"));
                student.setGroupName(rs.getString("group_name"));
                student.setUniqueNumber(rs.getString("unique_number"));
                students.add(student);
            }
        }
        return students;
    }
}

