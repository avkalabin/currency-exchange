package dao;

import model.Currency;
import model.ExchangeRate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao {

    public List<ExchangeRate> findAll() {
        String sql = """
                SELECT
                er.id, er.base_currency_id, er.target_currency_id, er.rate,
                bc.id as bc_id, bc.name as bc_name, bc.code as bc_code, bc.sign as bc_sign,
                tc.id as tc_id, tc.name as tc_name, tc.code as tc_code, tc.sign as tc_sign
                FROM exchange_rates er
                JOIN currencies bc ON er.base_currency_id = bc.id
                JOIN currencies tc ON er.target_currency_id = tc.id
                """;

        List<ExchangeRate> rates = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {
                Currency baseCurrency = new Currency(
                        rs.getInt("bc_id"),
                        rs.getString("bc_name"),
                        rs.getString("bc_code"),
                        rs.getString("bc_sign")

                );

                Currency targetCurrency = new Currency(
                        rs.getInt("tc_id"),
                        rs.getString("tc_name"),
                        rs.getString("tc_code"),
                        rs.getString("tc_sign")

                );

                rates.add(new ExchangeRate(
                        rs.getInt("id"),
                        baseCurrency,
                        targetCurrency,
                        rs.getDouble("rate")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get exchange rates", e);
        }
        return rates;
    }

    public ExchangeRate create(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        String sql = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";
        CurrencyDao currencyDao = new CurrencyDao();

        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(() -> new RuntimeException("Base currency not found " + baseCurrencyCode));

        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(() -> new RuntimeException("Target currency not found " + targetCurrencyCode));

        int baseCurrencyId = baseCurrency.id();
        int targetCurrencyId = targetCurrency.id();

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
                return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
            }
            throw new RuntimeException("Failed to get generated exchange rate ID");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create exchange rate", e);
        }
    }

    public boolean existByCurrencyPair(int baseCurrencyId, int targetCurrencyId) {
        String sql = "SELECT id FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, baseCurrencyId);
            pstmt.setInt(2, targetCurrencyId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to check exchange rates existence", e);
        }
    }
}
