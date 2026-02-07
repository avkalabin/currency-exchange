package dao;

import model.ExchangeRate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao {

    public List<ExchangeRate> findAll() {
        String sql = "SELECT id, base_currency_id, target_currency_id, rate FROM exchange_rates";
        List<ExchangeRate> rates = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {
                rates.add(new ExchangeRate(
                        rs.getInt("id"),
                        rs.getInt("base_currency_id"),
                        rs.getInt("target_currency_id"),
                        rs.getDouble("rate")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get exchange rates", e);
        }
        return rates;
    }

    public ExchangeRate create(int baseCurrencyId, int targetCurrencyId, double rate) {
        String sql = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            pstmt.setInt(1, baseCurrencyId);
            pstmt.setInt(2, targetCurrencyId);
            pstmt.setDouble(3, rate);
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                return new ExchangeRate(id, baseCurrencyId, targetCurrencyId, rate);
            }
            throw new RuntimeException("Failed to create exchange rate ID");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create exchange rate", e);
        }
    }
}
