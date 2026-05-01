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
        List<Currency> currencies = currencyService.findAllCurrencies();

        json(resp, SC_OK, currencies);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        if (name == null || code == null || sign == null
                || name.isEmpty() || code.isEmpty() || sign.isEmpty()) {
            error(resp, SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }

        try {
            Currency newCurrency = currencyService.createCurrency(name, code, sign);
            json(resp, HttpServletResponse.SC_CREATED, newCurrency);
        } catch (InvalidCurrencyCodeException e) {
            error(resp, SC_BAD_REQUEST, "Код валюты должен содержать 3 заглавные буквы A-Z");
        } catch (CurrencyAlreadyExistsException e) {
            error(resp, SC_CONFLICT, "Валюта с кодом " + code + " уже существует");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Unexpected error while creating currency", e);
            error(resp, SC_INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
        }
    }
}
