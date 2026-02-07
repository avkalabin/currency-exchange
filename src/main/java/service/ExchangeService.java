package service;

import dao.CurrencyRepository;
import dao.ExchangeRateRepository;
import model.Currency;
import model.ExchangeRate;

import java.util.Optional;

public class ExchangeService {

    private final CurrencyRepository currencyRepository = new CurrencyRepository();
    private final ExchangeRateRepository rateRepository = new ExchangeRateRepository();

    public Optional<ExchangeRate> findRate(String fromCode, String toCode) {
        Optional<Currency> fromCurrency = currencyRepository.findAll().stream()
                .filter(c -> c.code().equals(fromCode))
                .findFirst();

        Optional<Currency> toCurrency = currencyRepository.findAll().stream()
                .filter(c -> c.code().equals(toCode))
                .findFirst();

        if (fromCurrency.isEmpty() || toCurrency.isEmpty()) {
            return Optional.empty();
        }

        int fromId = fromCurrency.get().id();
        int toId = toCurrency.get().id();

        return rateRepository.findAll().stream()
                .filter(r -> r.baseCurrencyId() == fromId && r.targetCurrencyId() == toId)
                .findFirst();
    }

    public double convert(double amount, double rate) {
        return amount * rate;
    }
}
