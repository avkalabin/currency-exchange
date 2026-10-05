package dao;

import model.Currency;
import model.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateDao {

    List<ExchangeRate> findAll();

    ExchangeRate create(Currency baseCurrency, Currency targetCurrency, BigDecimal rate);

    Optional<ExchangeRate> findByCurrencyPair(String baseCode, String targetCode);

    ExchangeRate updateRateByCurrencyPair(String baseCode, String targetCode, BigDecimal rate);
}
