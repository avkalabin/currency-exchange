package servlet;

import exception.ExchangeRateNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRate;
import service.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.*;
import static util.ResponseUtil.error;
import static util.ResponseUtil.json;


@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private static final Logger log = Logger.getLogger(ExchangeRateServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/[A-Z]{6}")) {
            log.warning("Invalid currency pair in request " + pathInfo + " (expected /[A-Z]{6})");
            error(resp, SC_BAD_REQUEST, "Коды валют пары отсутствуют в адресе (в паре должно быть 6 заглавных букв A-Z)");
            return;
        }

        String baseCode = pathInfo.substring(1, 4);
        String targetCode = pathInfo.substring(4);

        try {
            Optional<ExchangeRate> exchangeRateOpt = exchangeRateService.findByCurrencyPair(baseCode, targetCode);
            if (exchangeRateOpt.isEmpty()) {
                log.warning("Exchange rate not found " + baseCode + targetCode);
                error(resp, SC_NOT_FOUND, "Обменный курс для пары не найден");
                return;
            }

            log.info("Exchange rate found: " + baseCode + targetCode);
            json(resp, SC_OK, exchangeRateOpt.get());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Unexpected error while fetching exchange rate for pair: " + baseCode + "/" + targetCode, e);
            error(resp, SC_INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
        }

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
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
            log.warning("Missed required form field");
            error(resp, SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }

        if (pathInfo == null || !pathInfo.matches("/[A-Z]{6}")) {
            log.warning("Invalid currency pair in request " + pathInfo + " (expected /[A-Z]{6})");
            error(resp, SC_BAD_REQUEST, "Коды валют пары отсутствуют в адресе (в паре должно быть 6 заглавных букв A-Z)");
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

        String baseCode = pathInfo.substring(1, 4);
        String targetCode = pathInfo.substring(4);

        try {
            Optional<ExchangeRate> exchangeRateOpt = exchangeRateService.findByCurrencyPair(baseCode, targetCode);
            if (exchangeRateOpt.isEmpty()) {
                log.warning("Exchange rate not found " + baseCode + targetCode);
                error(resp, SC_NOT_FOUND, "Обменный курс для пары не найден");
                return;
            }

            ExchangeRate updatedExchangeRate = exchangeRateService.updateRateByCurrencyPair(baseCode, targetCode, rate);
            log.info("Exchange rate updated " + baseCode + targetCode + " " + rate);
            json(resp, SC_OK, updatedExchangeRate);
        } catch (ExchangeRateNotFoundException e) {
            log.warning("Exchange rate not found " + baseCode + targetCode);
            error(resp, SC_NOT_FOUND, "Обменный курс для пары не найден");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Unexpected error while updating exchange rate for " + baseCode + "/" + targetCode, e);
            error(resp, SC_INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
        }
    }
}
