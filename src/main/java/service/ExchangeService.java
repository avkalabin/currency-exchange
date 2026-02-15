package service;

import dao.ExchangeRateDao;
import model.ExchangeRate;

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
            return Optional.of(new ExchangeRate(
                    0,
                    reverse.targetCurrency(),
                    reverse.baseCurrency(),
                    1.0 / reverse.rate()
            ));
        }

        Optional<ExchangeRate> usdToFrom = exchangeRateDao.findByCurrencyPair("USD", fromCode);
        Optional<ExchangeRate> usdToTo = exchangeRateDao.findByCurrencyPair("USD", toCode);

        if (usdToFrom.isPresent() && usdToTo.isPresent()) {
            double usdToFromRate = usdToFrom.get().rate();
            double usdToToRate = usdToTo.get().rate();
            double computedRate = usdToToRate / usdToFromRate;

            return Optional.of(new ExchangeRate(
                    -1,
                    usdToFrom.get().targetCurrency(),
                    usdToTo.get().targetCurrency(),
                    computedRate
            ));
        }

        return Optional.empty();
    }

    public double convert(double amount, double rate) {
        return amount * rate;
    }
}
