package service;

import dao.CurrencyDao;
import model.Currency;

import java.util.List;

public class CurrencyService {

    private final CurrencyDao currencyDao = new CurrencyDao();

    public final List<Currency> findAllCurrencies() {
        return currencyDao.findAll();
    }

    public final Currency createCurrency(String name, String code, String sign){
        return currencyDao.create(name, code, sign);
    }
}
