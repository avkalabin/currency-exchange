package service;

import model.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateService {

    List<ExchangeRate> findAll();

    ExchangeRate create(String baseCode, String targetCode, BigDecimal rate);

    Optional<ExchangeRate> findByCurrencyPair(String baseCode, String targetCode);

    ExchangeRate updateRateByCurrencyPair(String baseCode, String targetCode, BigDecimal rate);
}
