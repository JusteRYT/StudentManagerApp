package db;

import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для выполнения операций с базой данных для студентов.
 */
public class StudentDAO {

    /**
     * Проверяет, существует ли уникальный номер в базе данных.
     *
     * @param uniqueNumber уникальный номер студента
     * @return true, если номер существует, иначе false
     * @throws SQLException если не удалось выполнить запрос
     */
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
            statement.setString(4, student.getBirthDate());
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
            if (rowsAffected == 0) {
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
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setPatronymic(rs.getString("patronymic"));
                student.setBirthDate(rs.getDate("birth_date").toString());
                student.setGroupName(rs.getString("group_name"));
                student.setUniqueNumber(rs.getString("unique_number"));
                students.add(student);
            }
        }
        return students;
    }

    /**
     * Обновляет информацию о студенте.
     *
     * @param student объект Student с обновленной информацией
     * @throws SQLException если не удалось обновить информацию или если уникальный номер не указан
     */
    public void updateStudent(Student student) throws SQLException {
        if (student.getUniqueNumber() == null) {
            throw new SQLException("Unique number cannot be null.");
        }

        StringBuilder sql = new StringBuilder("UPDATE students SET ");
        List<Object> parameters = new ArrayList<>();

        if (student.getFirstName() != null) {
            sql.append("first_name = ?, ");
            parameters.add(student.getFirstName());
        }
        if (student.getLastName() != null) {
            sql.append("last_name = ?, ");
            parameters.add(student.getLastName());
        }
        if (student.getPatronymic() != null) {
            sql.append("patronymic = ?, ");
            parameters.add(student.getPatronymic());
        }
        if (student.getBirthDate() != null) {
            sql.append("birth_date = ?, ");
            parameters.add(student.getBirthDate());
        }
        if (student.getGroupName() != null) {
            sql.append("group_name = ?, ");
            parameters.add(student.getGroupName());
        }

        // Убираем последнюю запятую
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE unique_number = ?");
        parameters.add(student.getUniqueNumber());

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No student found with unique number: " + student.getUniqueNumber());
            }
        }
    }

    /**
     * Поиск студента по уникальному идентификатору.
     *
     * @param uniqueNumber уникальный идентификатор студента.
     * @return объект студента, если найден, иначе null.
     * @throws SQLException если произошла ошибка при обращении к базе данных.
     */
    public Student getStudentByUnique(String uniqueNumber) throws SQLException {
        String sql = "SELECT * FROM students WHERE unique_number = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, uniqueNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Создаем объект студента из результатов запроса
                    return new Student(
                            resultSet.getString("unique_number"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("patronymic"),
                            resultSet.getString("birth_date"),
                            resultSet.getString("group_name")
                    );
                }
            }
        }
        return null; // Если студент не найден
    }
}

