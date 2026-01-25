package service;

import dao.CurrencyDao;
import dao.impl.CurrencyDaoJdbc;
import model.Currency;

import java.util.List;
import java.util.Optional;

public class CurrencyService {

    CurrencyDao currencyDao = new CurrencyDaoJdbc();

    public List<Currency> getAllCurrencies() {
        return currencyDao.findAll();
    }

    public Optional<Currency> getCurrency(String code) {
        return currencyDao.findByCode(code);
    }
}



