package servlet;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRate;
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
        String fromParam = req.getParameter("from");
        String toParam = req.getParameter("to");
        String amountParam = req.getParameter("amount");

        if (fromParam == null || toParam == null || amountParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(gson.toJson(
                    Map.of("error", "Missing required query params: from, to, amount")
            ));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountParam);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("error", "Invalid amount format")));
            return;
        }

        Optional<ExchangeRate> rateOpt = exchangeService.findRate(fromParam, toParam);
        if (rateOpt.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(Map.of("error", "Exchange rate not found")));
            return;
        }

        ExchangeRate rate = rateOpt.get();
        double result = exchangeService.convert(amount, rate.rate());

        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(Map.of(
                "from", fromParam,
                "to", toParam,
                "rate", rate.rate(),
                "amount", amount,
                "convertedAmount", result
        )));
    }
}
