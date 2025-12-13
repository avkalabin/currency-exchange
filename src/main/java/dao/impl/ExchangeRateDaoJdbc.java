package dao.impl;

import dao.ExchangeRateDao;
import model.ExchangeRate;

import java.util.List;
import java.util.Optional;

public class ExchangeRateDaoJdbc implements ExchangeRateDao {

    @Override
    public List<ExchangeRate> findAll() {
        return List.of();
    }

    @Override
    public Optional<ExchangeRate> findByCurrencyCodes(String baseCode, String targetCode) {
        return Optional.empty();
    }

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        return null;
    }

    @Override
    public void update(ExchangeRate exchangeRate) {

    }
}
