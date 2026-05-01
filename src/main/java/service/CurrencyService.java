package service;

import dao.CurrencyDao;
import exception.CurrencyAlreadyExistsException;
import exception.DataAccessException;
import exception.InvalidCurrencyCodeException;
import exception.ServiceException;
import model.Currency;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {

    private final CurrencyDao currencyDao = new CurrencyDao();

    public final List<Currency> findAllCurrencies() {
        return currencyDao.findAll();
    }

    public final Currency createCurrency(String name, String code, String sign) {

        if (!code.matches("[A-Z]{3}")) {
           throw new InvalidCurrencyCodeException("Invalid currency code: must be 3 uppercase letters");
        }

        try {
            return currencyDao.create(name, code, sign);
        } catch (DataAccessException e) {
            if (e.getCause() instanceof SQLException &&
                    ((SQLException) e.getCause()).getErrorCode() == 19) {
                throw new CurrencyAlreadyExistsException("Currency already exists: " + code);
            }
            throw new ServiceException("Failed to create currency: " + code, e);
        }
    }
}
