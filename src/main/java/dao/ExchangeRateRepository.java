package dao;

import model.ExchangeRate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExchangeRateRepository {
    private static final List<ExchangeRate> rates = new ArrayList<>();
    private static final AtomicInteger idGenerator = new AtomicInteger(3);

    static {
        // USD -> EUR
        rates.add(new ExchangeRate(1, 1, 2, 0.9));
        // EUR -> USD
        rates.add(new ExchangeRate(2, 2, 1, 1.1));
        // USD -> RUB
        rates.add(new ExchangeRate(3, 1, 3, 90.0));
    }

    public List<ExchangeRate> findAll() {
        return new ArrayList<>(rates);
    }

    public ExchangeRate create(int baseCurrencyId, int targetCurrencyId, double rate) {
        int newId = idGenerator.incrementAndGet();
        ExchangeRate newExchangeRate = new ExchangeRate(newId, baseCurrencyId, targetCurrencyId, rate);
        rates.add(newExchangeRate);
        return newExchangeRate;
    }
}
