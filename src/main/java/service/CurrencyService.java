package service;

import model.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyService {

    List<Currency> findAllCurrencies();

    Currency createCurrency(String name, String code, String sign);

    Optional<Currency> findCurrencyByCode(String code);
}
