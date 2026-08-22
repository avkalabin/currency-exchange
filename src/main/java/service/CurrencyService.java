package service;

import dao.CurrencyDao;
import exception.*;
import model.Currency;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CurrencyService {

    private final CurrencyDao currencyDao = new CurrencyDao();

    public final List<Currency> findAllCurrencies() {
        return currencyDao.findAll();
    }

    public final Currency createCurrency(String name, String code, String sign) {
        if (!code.matches("[A-Z]{3}")) {
           throw new InvalidCurrencyCodeException("Invalid currency code: must be 3 uppercase letters");
        }

        if (sign.length() > 3) {
            throw new InvalidCurrencySignException("Invalid currency sign: must be 1-3 characters");
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

    public final Optional<Currency> findCurrencyByCode(String code) {
        if (!code.matches("[A-Z]{3}")) {
            throw new InvalidCurrencyCodeException("Invalid currency code: must be 3 uppercase letters");
        }

        try {
            return currencyDao.findByCode(code);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to find currency by code: " + code, e);
        }
    }
}
