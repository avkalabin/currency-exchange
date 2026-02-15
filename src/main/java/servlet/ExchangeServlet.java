package servlet;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRate;
import model.ExchangeResult;
import service.ExchangeService;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private final ExchangeService exchangeService = new ExchangeService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String fromParam = req.getParameter("from");
        String toParam = req.getParameter("to");
        String amountParam = req.getParameter("amount");

        if (fromParam == null || toParam == null || amountParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(
                    Map.of("message", "Отсутствует нужное поле формы")
            ));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountParam);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("message", "Недопустимый формат числа в запросе")));
            return;
        }

        Optional<ExchangeRate> rateOpt = exchangeService.findRate(fromParam, toParam);
        if (rateOpt.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(Map.of("message", "Курс обмена не найден для пары валют")));
            return;
        }

        ExchangeRate exchangeRate = rateOpt.get();
        double result = exchangeService.convert(amount, exchangeRate.rate());

        ExchangeResult exchangeResult = new ExchangeResult(
                exchangeRate.baseCurrency(),
                exchangeRate.targetCurrency(),
                exchangeRate.rate(),
                amount,
                result
        );

        resp.getWriter().write(gson.toJson(exchangeResult));
    }
}
