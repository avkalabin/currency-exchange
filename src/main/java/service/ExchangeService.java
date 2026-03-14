package service;

import dao.ExchangeRateDao;
import model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeService {

    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    public Optional<ExchangeRate> findRate(String fromCode, String toCode) {
        Optional<ExchangeRate> directRate = exchangeRateDao.findByCurrencyPair(fromCode, toCode);
        if (directRate.isPresent()) {
            return directRate;
        }

        Optional<ExchangeRate> reverseRate = exchangeRateDao.findByCurrencyPair(toCode, fromCode);
        if (reverseRate.isPresent()) {
            ExchangeRate reverse = reverseRate.get();
            BigDecimal inverseRate = BigDecimal.ONE.divide(reverse.rate(), 10, RoundingMode.HALF_EVEN);
            return Optional.of(new ExchangeRate(
                    0,
                    reverse.targetCurrency(),
                    reverse.baseCurrency(),
                    inverseRate
            ));
        }

        Optional<ExchangeRate> usdToFrom = exchangeRateDao.findByCurrencyPair("USD", fromCode);
        Optional<ExchangeRate> usdToTo = exchangeRateDao.findByCurrencyPair("USD", toCode);

        if (usdToFrom.isPresent() && usdToTo.isPresent()) {
            BigDecimal usdToFromRate = usdToFrom.get().rate();
            BigDecimal usdToToRate = usdToTo.get().rate();
            BigDecimal computedRate = usdToToRate.divide(usdToFromRate, 10, RoundingMode.HALF_EVEN);

            return Optional.of(new ExchangeRate(
                    -1,
                    usdToFrom.get().targetCurrency(),
                    usdToTo.get().targetCurrency(),
                    computedRate
            ));
        }

        return Optional.empty();
    }

    public BigDecimal convert(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate)
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
