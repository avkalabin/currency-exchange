package servlet;

import com.google.gson.Gson;
import dao.ExchangeRateDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRate;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    private final Gson gson = new Gson();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/[A-Z]{6}")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("message", "Коды валют пары отсутствуют в адресе (в паре должно быть 6 заглавных букв A-Z)")));
            return;
        }

        String baseCode = pathInfo.substring(1, 4);
        String targetCode = pathInfo.substring(4);

        Optional<ExchangeRate> exchangeRateOpt = exchangeRateDao.findByCurrencyPair(baseCode, targetCode);
        if (exchangeRateOpt.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(Map.of("message", "Обменный курс для пары не найден")));
            return;
        }

        resp.getWriter().write(gson.toJson(exchangeRateOpt.get()));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String pathInfo = req.getPathInfo();
        String contentType = req.getContentType();
        String rateParam = null;

        if (contentType != null && contentType.contains("x-www-form-urlencoded")) {

            String body = req.getReader().lines().collect(Collectors.joining());
            String[] pairs = body.split("&");

            for (String pair : pairs) {
                if (pair.startsWith("rate=")) {
                    String[] parts = pair.split("=", 2);
                        rateParam = parts[1];
                }
            }
        }

        if (rateParam == null || rateParam.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("message", "Отсутствует нужное поле формы")));
            return;
        }

        if (pathInfo == null || !pathInfo.matches("/[A-Z]{6}")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("message", "Коды валют пары отсутствуют в адресе (в паре должно быть 6 заглавных букв A-Z)")));
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

        String baseCode = pathInfo.substring(1, 4);
        String targetCode = pathInfo.substring(4);

        Optional<ExchangeRate> exchangeRateOpt = exchangeRateDao.findByCurrencyPair(baseCode, targetCode);
        if (exchangeRateOpt.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(Map.of("message", "Обменный курс для пары не найден")));
            return;
        }

        ExchangeRate updatedExchangeRate = exchangeRateDao.updateRateByCurrencyPair(baseCode, targetCode, rate);
        resp.getWriter().write(gson.toJson(updatedExchangeRate));
    }
}
