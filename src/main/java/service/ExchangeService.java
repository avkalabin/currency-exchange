package service;

import model.ExchangeRate;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExchangeService {

    Optional<ExchangeRate> findRate(String fromCode, String toCode);

    BigDecimal convert(BigDecimal amount, BigDecimal rate);
}
