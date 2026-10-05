package service.impl;

import dao.CurrencyDao;
import dao.ExchangeRateDao;
import model.ExchangeRate;
import service.ExchangeService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeRateDao exchangeRateDao;
    private final CurrencyDao currencyDao;

    public ExchangeServiceImpl(ExchangeRateDao exchangeRateDao, CurrencyDao currencyDao) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyDao = currencyDao;
    }

    public Optional<ExchangeRate> findRate(String fromCode, String toCode) {
        if (fromCode.equals(toCode)) {
            return currencyDao.findByCode(fromCode)
                    .map(currency -> new ExchangeRate(0, currency, currency, BigDecimal.ONE));
        }

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
                    0,
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
