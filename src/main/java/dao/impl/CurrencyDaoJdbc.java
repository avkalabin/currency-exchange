package dao.impl;

import dao.CurrencyDao;
import util.DatabaseConnection;
import model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDaoJdbc implements CurrencyDao {

    @Override
    public List<Currency> findAll() {
        String sql = "SELECT ID, CODE, FULL_NAME, SIGN FROM CURRENCIES";
        List<Currency> result = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения списка валют", e);
        }
        return result;
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        String sql = "SELECT ID, CODE, FULL_NAME, SIGN FROM CURRENCIES WHERE CODE = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска валюты по коду: " + code, e);
        }
        return Optional.empty();
    }

    @Override
    public Currency save(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("Валюта не передана");
        }
        String sql = "INSERT INTO CURRENCIES(CODE, FULL_NAME, SIGN) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, currency.getCode());
            ps.setString(2, currency.getFullName());
            ps.setString(3, currency.getSign());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Не удалось создать валюту");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    currency.setId(id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения валюты: " + e.getMessage(), e);
        }
        return currency;
    }

    private Currency mapRow(ResultSet rs) throws SQLException {
        Currency currency = new Currency();
        currency.setId(rs.getInt("ID"));
        currency.setCode(rs.getString("CODE"));
        currency.setFullName(rs.getString("FULL_NAME"));
        currency.setSign(rs.getString("SIGN"));
        return currency;
    }
}
