package dao;

import model.ExchangeRate;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateDao {

    List<ExchangeRate> findAll();

    Optional<ExchangeRate> findByCurrencyCodes(String baseCode, String targetCode);

    ExchangeRate save(ExchangeRate exchangeRate);

    void update(ExchangeRate exchangeRate);
}
