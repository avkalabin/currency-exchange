package servlet;

import com.google.gson.Gson;
import dao.ExchangeRateDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRate;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    private final Gson gson = new Gson();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");

        String pathInfo = req.getPathInfo();
        System.out.println(pathInfo);
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
}
