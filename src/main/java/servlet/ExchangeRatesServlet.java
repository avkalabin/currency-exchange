package servlet;

import exception.CurrencyNotFoundException;
import exception.ExchangeRateAlreadyExistsException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRate;
import service.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jakarta.servlet.http.HttpServletResponse.*;
import static util.ResponseUtil.error;
import static util.ResponseUtil.json;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private static final Logger log = Logger.getLogger(ExchangeRatesServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            List<ExchangeRate> rates = exchangeRateService.findAll();

            log.info("All exchange rates found");
            json(resp, SC_OK, rates);
        } catch (Exception e) {
            log.log(Level.SEVERE, " Unexpected error while fetching all exchange rates", e);
            error(resp, SC_INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateParam = req.getParameter("rate");

        if (baseCurrencyCode == null || targetCurrencyCode == null || rateParam == null) {
            log.warning("Missed required form field");
            error(resp, SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }

        if (!baseCurrencyCode.matches("[A-Z]{3}") || !targetCurrencyCode.matches("[A-Z]{3}")) {
            log.warning("Invalid currency code: must be 3 uppercase letters");
            error(resp, SC_BAD_REQUEST, "Код валюты отсутствует в адресе (должен содержать 3 заглавные буквы A-Z)");
            return;
        }

        BigDecimal rate;

        try {
            rate = new BigDecimal(rateParam);
        } catch (NumberFormatException e) {
            log.warning("Invalid number format in request");
            error(resp, SC_BAD_REQUEST, "Недопустимый формат числа в запросе");
            return;
        }

        if (rate.signum() <= 0) {
            log.warning("Exchange rate must be greater than zero");
            error(resp, SC_BAD_REQUEST, "Курс обмена должен быть больше нуля");
            return;
        }

        try {
            ExchangeRate newRate = exchangeRateService.create(baseCurrencyCode, targetCurrencyCode, rate);
            log.info("New exchange rate created " + baseCurrencyCode + targetCurrencyCode + " " + rate);
            json(resp, SC_CREATED, newRate);
        } catch (CurrencyNotFoundException e) {
            log.warning("Currency pair not found in DB: " + baseCurrencyCode + "/" + targetCurrencyCode);
            error(resp, SC_NOT_FOUND, "Одна (или обе) валюта из валютной пары не существует в БД");
        } catch (ExchangeRateAlreadyExistsException e) {
            log.warning("Exchange rate pair already exists: " + baseCurrencyCode + "/" + targetCurrencyCode);
            error(resp, SC_CONFLICT, "Валютная пара с таким кодом уже существует");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Unexpected error while creating exchange rate", e);
            error(resp, SC_INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
        }
    }
}
