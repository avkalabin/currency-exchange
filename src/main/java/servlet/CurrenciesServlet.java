package servlet;

import dao.CurrencyDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;
import static util.ResponseUtil.error;
import static util.ResponseUtil.json;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Currency> currencies = currencyDao.findAll();

        json(resp, SC_OK, currencies);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        if (name.isEmpty() || code.isEmpty() || sign.isEmpty()) {
            error(resp, SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }

        if (!code.matches("[A-Z]{3}")) {
            error(resp, SC_BAD_REQUEST, "Код валюты должен содержать 3 заглавные буквы A-Z");
            return;
        }

        try {
            Currency newCurrency = currencyDao.create(name, code, sign);
            json(resp, HttpServletResponse.SC_CREATED, newCurrency);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SQLException &&
                    ((SQLException) e.getCause()).getErrorCode() == 19) {
                error(resp, SC_CONFLICT, "Валюта с таким кодом уже существует");
            } else {
                error(resp, SC_INTERNAL_SERVER_ERROR, "Ошибка сервера");
            }
        }
    }
}
