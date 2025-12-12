package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:currency_exchange.db";
    private static Connection connection = null;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    private DatabaseConnection() {
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                connection.createStatement().execute("PRAGMA foreign_keys = ON");
                logger.info("Подключение к SQLite успешно установлено");
            }
            return connection;
        } catch (SQLException e) {
            logger.error("Не удалось установить подключение к базе данных");
            throw new RuntimeException(e);
        }
    }

}
