package dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void init() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            try (InputStream is = DatabaseInitializer.class.getClassLoader().getResourceAsStream("schema.sql")) {

                if (is == null) {
                    throw new RuntimeException("schema.sql not found in resources");
                }

                String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                String[] statements = sql.split(";");
                for (String statement : statements) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.addBatch(trimmed);
                    }
                }

                stmt.executeBatch();
                System.out.println("Executed " + statements.length + " SQL statements");
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}
