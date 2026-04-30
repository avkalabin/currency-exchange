package servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRate;
import model.ExchangeResult;
import service.ExchangeService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.*;
import static util.ResponseUtil.error;
import static util.ResponseUtil.json;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private final ExchangeService exchangeService = new ExchangeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fromParam = req.getParameter("from");
        String toParam = req.getParameter("to");
        String amountParam = req.getParameter("amount");

        if (fromParam == null || toParam == null || amountParam == null) {
            error(resp, SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountParam);
        } catch (NumberFormatException e) {
            error(resp, SC_BAD_REQUEST, "Недопустимый формат числа в запросе");
            return;
        }

        Optional<ExchangeRate> rateOpt = exchangeService.findRate(fromParam, toParam);
        if (rateOpt.isEmpty()) {
            error(resp, SC_NOT_FOUND, "Курс обмена не найден для пары валют");
            return;
        }

        ExchangeRate exchangeRate = rateOpt.get();
        BigDecimal result = exchangeService.convert(amount, exchangeRate.rate());

        ExchangeResult exchangeResult = new ExchangeResult(
                exchangeRate.baseCurrency(),
                exchangeRate.targetCurrency(),
                exchangeRate.rate(),
                amount,
                result
        );

        json(resp, SC_OK, exchangeResult);
    }
}
