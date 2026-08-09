package servlet;

import exception.CurrencyAlreadyExistsException;
import exception.InvalidCurrencyCodeException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import service.CurrencyService;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jakarta.servlet.http.HttpServletResponse.*;
import static util.ResponseUtil.error;
import static util.ResponseUtil.json;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyService();
    private static final Logger log = Logger.getLogger(CurrenciesServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Currency> currencies = currencyService.findAllCurrencies();

            log.info("All currencies found");
            json(resp, SC_OK, currencies);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Unexpected error while fetching all currencies", e);
            error(resp, SC_INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        if (name == null || code == null || sign == null
                || name.isEmpty() || code.isEmpty() || sign.isEmpty()) {
            log.warning("Missing required form field");
            error(resp, SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }

        try {
            Currency newCurrency = currencyService.createCurrency(name, code, sign);
            log.info("New currency created: " + code + " " + name);
            json(resp, HttpServletResponse.SC_CREATED, newCurrency);
        } catch (InvalidCurrencyCodeException e) {
            log.warning("Invalid currency code: must be 3 uppercase letters");
            error(resp, SC_BAD_REQUEST, "Код валюты должен содержать 3 заглавные буквы A-Z");
        } catch (CurrencyAlreadyExistsException e) {
            log.warning("Currency with code " + code + " already exist");
            error(resp, SC_CONFLICT, "Валюта с кодом " + code + " уже существует");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Unexpected error while creating currency", e);
            error(resp, SC_INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
        }
    }
}
