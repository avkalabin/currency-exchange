package servlet;

import com.google.gson.Gson;
import dao.ExchangeRateDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
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

        String baseCurrencyIdParam = req.getParameter("baseCurrencyId");
        String targetCurrencyIdParam = req.getParameter("targetCurrencyId");
        String rateParam = req.getParameter("rate");

        if (baseCurrencyIdParam == null || targetCurrencyIdParam == null || rateParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("error", "Missing required fields")));
            return;
        }

        int baseCurrencyId;
        int targetCurrencyId;
        double rate;

        try {
            baseCurrencyId = Integer.parseInt(baseCurrencyIdParam);
            targetCurrencyId = Integer.parseInt(targetCurrencyIdParam);
            rate = Double.parseDouble(rateParam);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("error", "Invalid number format")));
            return;
        }

        ExchangeRate newRate = exchangeRateDao.create(baseCurrencyId, targetCurrencyId, rate);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(newRate));
    }
}
