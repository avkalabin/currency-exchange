package servlet;

import com.google.gson.Gson;
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
import java.util.Map;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    private final CurrencyDao currencyDao = new CurrencyDao();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRate> rates = exchangeRateDao.findAll();

        resp.getWriter().write(gson.toJson(rates));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateParam = req.getParameter("rate");

        if (baseCurrencyCode == null || targetCurrencyCode == null || rateParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("message", "Отсутствует нужное поле формы")));
            return;
        }

        if (!baseCurrencyCode.matches("[A-Z]{3}") || !targetCurrencyCode.matches("[A-Z]{3}")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("message", "Код валюты отсутствует в адресе (должен содержать 3 заглавные буквы A-Z)")));
            return;
        }

        var baseCurrencyOpt = currencyDao.findByCode(baseCurrencyCode);
        var targetCurrencyOpt = currencyDao.findByCode(targetCurrencyCode);

        if (baseCurrencyOpt.isEmpty() || targetCurrencyOpt.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(Map.of("message", "Одна (или обе) валюта из валютной пары не существует в БД")));
            return;
        }

        BigDecimal rate;

        try {
            rate = new BigDecimal(rateParam);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("message", "Недопустимый формат числа в запросе")));
            return;
        }

        try {
            ExchangeRate newRate = exchangeRateDao.create(baseCurrencyCode, targetCurrencyCode, rate);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(newRate));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SQLException &&
            ((SQLException) e.getCause()).getErrorCode() == 19) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write(gson.toJson(Map.of("message", "Курс для этой пары валют уже существует")));
            }else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(Map.of("message", "Ошибка сервера")));
            }
        }
    }
}
