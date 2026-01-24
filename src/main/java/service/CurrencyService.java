package service;

import dao.CurrencyDao;
import dao.impl.CurrencyDaoJdbc;
import model.Currency;

import java.util.List;

public class CurrencyService {

    CurrencyDao currencyDao = new CurrencyDaoJdbc();

    public List<Currency> getAllCurrencies() {
        return currencyDao.findAll();
    }
}



