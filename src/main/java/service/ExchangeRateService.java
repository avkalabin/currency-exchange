package service;

import dao.CurrencyDao;
import dao.ExchangeRateDao;
import exception.CurrencyNotFoundException;
import exception.DataAccessException;
import exception.ExchangeRateAlreadyExistsException;
import exception.ServiceException;
import model.Currency;
import model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {

    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    private final CurrencyDao currencyDao = new CurrencyDao();

    public List<ExchangeRate> findAll() {
        return exchangeRateDao.findAll();
    }

    public ExchangeRate create(String baseCode, String targetCode, BigDecimal rate) {
        try {
            Currency base = currencyDao.findByCode(baseCode).orElseThrow(() -> new CurrencyNotFoundException("Base currency not found " + baseCode));
            Currency target = currencyDao.findByCode(targetCode).orElseThrow(() -> new CurrencyNotFoundException("Target currency not found " + targetCode));
            return exchangeRateDao.create(base, target, rate);
        } catch (DataAccessException e) {
            if (e.getCause() instanceof SQLException sqlException && sqlException.getErrorCode() == 19) {
                throw new ExchangeRateAlreadyExistsException("Exchange rate already exists " + baseCode + targetCode);
            }
            throw new ServiceException("Failed to create exchange rate", e);
        }
    }

    public Optional<ExchangeRate> findByCurrencyPair(String baseCode, String targetCode) {
        return exchangeRateDao.findByCurrencyPair(baseCode, targetCode);
    }

    public ExchangeRate updateRateByCurrencyPair(String baseCode, String targetCode, BigDecimal rate) {
        return exchangeRateDao.updateRateByCurrencyPair(baseCode, targetCode, rate);
    }
}
