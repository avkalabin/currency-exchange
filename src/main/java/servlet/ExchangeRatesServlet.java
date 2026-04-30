package servlet;

import dao.CurrencyDao;
import dao.ExchangeRateDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRate;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;
import static util.ResponseUtil.error;
import static util.ResponseUtil.json;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    private final CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRate> rates = exchangeRateDao.findAll();

        json(resp, SC_OK, rates);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateParam = req.getParameter("rate");

        if (baseCurrencyCode == null || targetCurrencyCode == null || rateParam == null) {
            error(resp, SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }

        if (!baseCurrencyCode.matches("[A-Z]{3}") || !targetCurrencyCode.matches("[A-Z]{3}")) {
            error(resp, SC_BAD_REQUEST, "Код валюты отсутствует в адресе (должен содержать 3 заглавные буквы A-Z)");
            return;
        }

        var baseCurrencyOpt = currencyDao.findByCode(baseCurrencyCode);
        var targetCurrencyOpt = currencyDao.findByCode(targetCurrencyCode);

        if (baseCurrencyOpt.isEmpty() || targetCurrencyOpt.isEmpty()) {
            error(resp, SC_NOT_FOUND, "Одна (или обе) валюта из валютной пары не существует в БД");
            return;
        }

        BigDecimal rate;

        try {
            rate = new BigDecimal(rateParam);
        } catch (NumberFormatException e) {
            error(resp, SC_BAD_REQUEST, "Недопустимый формат числа в запросе");
            return;
        }

        try {
            ExchangeRate newRate = exchangeRateDao.create(baseCurrencyCode, targetCurrencyCode, rate);
            json(resp, SC_CREATED, newRate);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SQLException &&
            ((SQLException) e.getCause()).getErrorCode() == 19) {
                error(resp, SC_CONFLICT, "Курс для этой пары валют уже существует");
            }else {
                error(resp,SC_INTERNAL_SERVER_ERROR, "Ошибка сервера");
            }
        }
    }
}
