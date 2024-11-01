package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Класс для управления подключением к базе данных.
 */
public class DatabaseConfig {
    private static final String URL = System.getenv("DB_URL") != null
            ? System.getenv("DB_URL")
            : "jdbc:mysql://localhost:3306/StudentDB";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    /**
     * Устанавливаем соединение с базой данных
     *
     * @return Объект Connection для работы с БД
     * @throws SQLException если не удалось подключится к БД
     */
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL, USER,PASSWORD);
    }
}
