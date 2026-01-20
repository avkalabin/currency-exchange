package dao.impl;

import dao.DatabaseConnection;
import dao.ExchangeRateDao;
import model.ExchangeRate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDaoJdbc implements ExchangeRateDao {

    @Override
    public List<ExchangeRate> findAll() {
        String sql = "SELECT ID, BASE_CURRENCY_ID, TARGET_CURRENCY_ID, RATE FROM EXCHANGE_RATES";
        List<ExchangeRate> results = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения списка курса обмена валют", e);
        }
        return results;
    }

    @Override
    public Optional<ExchangeRate> findByCurrencyCodes(String baseCode, String targetCode) {
        String sql = "SELECT er.ID, er.BASE_CURRENCY_ID, er.TARGET_CURRENCY_ID, er.RATE FROM EXCHANGE_RATES er " +
                     "JOIN CURRENCIES bc ON er.BASE_CURRENCY_ID = bc.ID " +
                     "JOIN CURRENCIES tc ON er.TARGET_CURRENCY_ID = tc.ID " +
                     "WHERE bc.CODE = ? AND tc.CODE = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, baseCode);
            ps.setString(2, targetCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска курса обмена по кодам", e);
        }
        return Optional.empty();
    }

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        String sql = "INSERT INTO EXCHANGE_RATES (BASE_CURRENCY_ID, TARGET_CURRENCY_ID, RATE) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, exchangeRate.getBaseCurrencyId());
            ps.setInt(2, exchangeRate.getTargetCurrencyId());
            ps.setDouble(3, exchangeRate.getRate());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Не удалось сохранить курс валют");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    exchangeRate.setId(id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения курса обмена: " + e.getMessage(), e);
        }
        return exchangeRate;
    }

    @Override
    public void update(ExchangeRate exchangeRate) {
        String sql = "UPDATE EXCHANGE_RATES SET BASE_CURRENCY_ID = ?, TARGET_CURRENCY_ID = ?, RATE = ? WHERE ID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, exchangeRate.getBaseCurrencyId());
            ps.setInt(2, exchangeRate.getTargetCurrencyId());
            ps.setDouble(3, exchangeRate.getRate());
            ps.setInt(4, exchangeRate.getId());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Не удалось обновить курс валют");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления курса обмена: " + e.getMessage(), e);
        }

    }

    private ExchangeRate mapRow(ResultSet rs) throws SQLException {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setId(rs.getInt("ID"));
        exchangeRate.setBaseCurrencyId(rs.getInt("BASE_CURRENCY_ID"));
        exchangeRate.setTargetCurrencyId(rs.getInt("TARGET_CURRENCY_ID"));
        exchangeRate.setRate(rs.getDouble("RATE"));
        return exchangeRate;
    }
}
