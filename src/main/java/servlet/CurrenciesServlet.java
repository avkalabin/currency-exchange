package servlet;

import com.google.gson.Gson;
import dao.CurrencyDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyDao currencyDao = new CurrencyDao();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Currency> currencies = currencyDao.findAll();

        String json = gson.toJson(currencies);
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        if (name.isEmpty() || code.isEmpty() || sign.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("message", "Отсутствует нужное поле формы")));
            return;
        }

        if (!code.matches("[A-Z]{3}")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("message", "Код валюты должен содержать 3 заглавные буквы A-Z")));
            return;
        }

        try {
            Currency newCurrency = currencyDao.create(name, code, sign);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(newCurrency));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SQLException &&
                    ((SQLException) e.getCause()).getErrorCode() == 19) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write(gson.toJson(Map.of("message", "Валюта с таким кодом уже существует")));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(Map.of("message", "Ошибка сервера")));
            }
        }
    }
}
