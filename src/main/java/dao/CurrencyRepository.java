package dao;


import model.Currency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CurrencyRepository {
    private static final List<Currency> currencies = new ArrayList<>();
    private static final AtomicInteger idGenerator = new AtomicInteger(3);

    static {
        currencies.add(new Currency(1, "United States dollar", "USD", "$"));
        currencies.add(new Currency(2, "Euro", "EUR", "€"));
        currencies.add(new Currency(3, "Russian Ruble", "RUB", "₽"));
    }

    public List<Currency> findAll() {
        return new ArrayList<>(currencies);
    }

    public Currency create(String name, String code, String sign) {
        int newId = idGenerator.incrementAndGet();
        Currency newCurrency = new Currency(newId, name, code, sign);
        currencies.add(newCurrency);
        return newCurrency;
    }
}
