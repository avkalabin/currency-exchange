package servlet;

import dao.ExchangeRateDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.*;
import static util.ResponseUtil.error;
import static util.ResponseUtil.json;


@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/[A-Z]{6}")) {
            error(resp, SC_BAD_REQUEST, "Коды валют пары отсутствуют в адресе (в паре должно быть 6 заглавных букв A-Z)");
            return;
        }

        String baseCode = pathInfo.substring(1, 4);
        String targetCode = pathInfo.substring(4);

        Optional<ExchangeRate> exchangeRateOpt = exchangeRateDao.findByCurrencyPair(baseCode, targetCode);
        if (exchangeRateOpt.isEmpty()) {
            error(resp, SC_NOT_FOUND,"Обменный курс для пары не найден");
            return;
        }

        json(resp, SC_OK, exchangeRateOpt.get());
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
            error(resp, SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }

        if (pathInfo == null || !pathInfo.matches("/[A-Z]{6}")) {
            error(resp, SC_BAD_REQUEST, "Коды валют пары отсутствуют в адресе (в паре должно быть 6 заглавных букв A-Z)");
            return;
        }

        BigDecimal rate;

        try {
            rate = new BigDecimal(rateParam);
        } catch (NumberFormatException e) {
            error(resp, SC_BAD_REQUEST, "Недопустимый формат числа в запросе");
            return;
        }

        String baseCode = pathInfo.substring(1, 4);
        String targetCode = pathInfo.substring(4);

        Optional<ExchangeRate> exchangeRateOpt = exchangeRateDao.findByCurrencyPair(baseCode, targetCode);
        if (exchangeRateOpt.isEmpty()) {
            error(resp, SC_NOT_FOUND, "Обменный курс для пары не найден");
            return;
        }

        ExchangeRate updatedExchangeRate = exchangeRateDao.updateRateByCurrencyPair(baseCode, targetCode, rate);
        json(resp, SC_OK, updatedExchangeRate);
    }
}
