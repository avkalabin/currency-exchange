package service;

import dao.CurrencyDao;
import dao.ExchangeRateDao;
import model.Currency;
import model.ExchangeRate;

import java.util.Optional;

public class ExchangeService {

    private final CurrencyDao currencyDao = new CurrencyDao();
    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    public Optional<ExchangeRate> findRate(String fromCode, String toCode) {
        Optional<Currency> fromCurrency = currencyDao.findAll().stream()
                .filter(c -> c.code().equals(fromCode))
                .findFirst();

        Optional<Currency> toCurrency = currencyDao.findAll().stream()
                .filter(c -> c.code().equals(toCode))
                .findFirst();

        if (fromCurrency.isEmpty() || toCurrency.isEmpty()) {
            return Optional.empty();
        }

        int fromId = fromCurrency.get().id();
        int toId = toCurrency.get().id();

        return exchangeRateDao.findAll().stream()
                .filter(r -> r.baseCurrencyId() == fromId && r.targetCurrencyId() == toId)
                .findFirst();
    }

    public double convert(double amount, double rate) {
        return amount * rate;
    }
}
