package servlet;

import com.google.gson.Gson;
import dao.CurrencyDao;
import dao.ExchangeRateDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import model.ExchangeRate;

import java.io.IOException;
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

        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(rates));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

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

        double rate;

        try {
            rate = Double.parseDouble(rateParam);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("message", "Недопустимый формат числа в запросе")));
            return;
        }

        Currency baseCurrency = baseCurrencyOpt.get();
        Currency targetCurrency = targetCurrencyOpt.get();

        if (exchangeRateDao.existByCurrencyPair(baseCurrency.id(), targetCurrency.id())) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(gson.toJson(Map.of("message", "Курс для этой пары валют уже существует")));
            return;
        }

        ExchangeRate newRate = exchangeRateDao.create(baseCurrencyCode, targetCurrencyCode, rate);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(newRate));
    }
}
