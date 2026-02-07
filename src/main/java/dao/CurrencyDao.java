package dao;

import model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao {
    public List<Currency> findAll() {
        String sql = "SELECT id, name, code, sign FROM currencies";
        List<Currency> currencies = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                currencies.add(new Currency(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("code"),
                        rs.getString("sign")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get currencies", e);
        }
        return currencies;
    }

    public Currency create(String name, String code, String sign) {
        String sql = "INSERT INTO currencies (name, code, sign) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            pstmt.setString(1, name);
            pstmt.setString(2, code);
            pstmt.setString(3, sign);
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                return new Currency(id, name, code, sign);
            }

            throw new RuntimeException("Failed to create currency ID");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
