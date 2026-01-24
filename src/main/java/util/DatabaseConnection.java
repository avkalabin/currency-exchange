package util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:currency_exchange.db";
    private static Connection connection = null;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static boolean isDbInitialized;

    private DatabaseConnection() {
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                 connection.createStatement().execute("PRAGMA foreign_keys = ON");
                logger.info("Подключение к SQLite успешно установлено");
            }
            if (!isDbInitialized) {
                initDb();
                isDbInitialized = true;
            }
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("Не удалось установить подключение к базе данных");
            throw new RuntimeException(e);
        }
    }

    private static void initDb() {
        String sql = readResource();
        String[] statements = sql.split(";");
        for (String s : statements) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) {
                try (Statement st = connection.createStatement()) {
                    st.executeUpdate(trimmed);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static String readResource() {
        try (var in = DatabaseConnection.class.getClassLoader()
                                              .getResourceAsStream("schema.sql")) {
            if (in == null) {
                throw new IOException("schema.sql not found");
            }
            return new String(in.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
