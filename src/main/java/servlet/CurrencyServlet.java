package servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import service.CurrencyService;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jakarta.servlet.http.HttpServletResponse.*;
import static util.ResponseUtil.error;
import static util.ResponseUtil.json;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyService();
    private static final Logger log = Logger.getLogger(CurrenciesServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/[A-Z]{3}")) {
            log.warning("Invalid currency code: must be 3 uppercase letters");
            error(resp, SC_BAD_REQUEST, "Код валюты отсутствует в адресе (должен содержать 3 заглавные буквы A-Z)");
            return;
        }

        String code = pathInfo.substring(1);

        try {
            Optional<Currency> currency = currencyService.findCurrencyByCode(code);
            if (currency.isEmpty()) {
                log.warning("Failed to find currency by code: " + code);
                error(resp, SC_NOT_FOUND, "Валюта не найдена");
                return;
            }
            log.info("Currency found by code: " + code);
            json(resp, SC_OK, currency.get());

        } catch (Exception e) {
            log.log(Level.SEVERE, "Unexpected error while fetching currency", e);
            error(resp, SC_INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
        }
    }
}
