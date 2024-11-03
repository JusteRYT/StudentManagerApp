package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Класс для управления подключением к базе данных.
 */
public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/StudentDB";
    private static final String USER = "root"; //Естественно не лучшее решение для хранения логина и пароля, но для тестового задания я сделал так
    private static final String PASSWORD = "root";

    private static Connection connection;

    /**
     * Получает соединение с базой данных.
     * Если соединение уже установлено, возвращает его.
     *
     * @return Объект Connection для работы с БД
     * @throws SQLException если не удалось подключиться к БД
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    /**
     * Закрывает соединение с базой данных, если оно открыто.
     *
     * @throws SQLException если возникла ошибка при закрытии соединения
     */
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
