package servlet;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import service.CurrencyService;

import java.io.IOException;
import java.util.Optional;

import static util.ResponseUtil.setErrorResponse;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService service = new CurrencyService();
    private final Gson gson = new Gson();

    private static final int CURRENCY_CODE_LENGTH = 3;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String pathParameter = req.getPathInfo();

        if (pathParameter == null || pathParameter.equals("/")) {
            setErrorResponse(resp, 400, "Код валюты отсутствует в адресе");
            return;
        }

        String currencyCode = pathParameter.replace("/", "");
        if (currencyCode.length() != CURRENCY_CODE_LENGTH) {
            setErrorResponse(resp, 400, "Код валюты должен состоять из 3 символов");
            return;
        }

        Optional<Currency> currency = service.getCurrency(currencyCode.toUpperCase());
        if (currency.isPresent()) {
            resp.getWriter().write(gson.toJson(currency.get()));
        } else {
            setErrorResponse(resp, 404, "Валюта не найдена");
        }
    }
}