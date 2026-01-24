package servlet;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;

import java.io.IOException;

import static util.ResponseUtil.setErrorMessage;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService service = new CurrencyService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        String pathParameter = req.getPathInfo();
        if (pathParameter == null) {
            try {
                setErrorMessage(resp, 400, "Код валюты отсутствует в адресе");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String currencyCode = pathParameter.replace("/", "");
        System.out.println(currencyCode);
//        List<Currency> currencies = service.getCurrency(code);
//        try {
//            resp.getWriter().write(gson.toJson(currencies));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }


}