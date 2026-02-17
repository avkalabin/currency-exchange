    package servlet;

    import com.google.gson.Gson;
    import dao.CurrencyDao;
    import jakarta.servlet.annotation.WebServlet;
    import jakarta.servlet.http.HttpServlet;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import model.Currency;

    import java.io.IOException;
    import java.util.Map;
    import java.util.Optional;

    @WebServlet("/currency/*")
    public class CurrencyServlet extends HttpServlet {
        private final CurrencyDao currencyDao = new CurrencyDao();
        private final Gson gson = new Gson();

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setContentType("application/json; charset=UTF-8");

            String pathInfo = req.getPathInfo();
            if (pathInfo == null || !pathInfo.matches("/[A-Z]{3}")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(Map.of("message", "Код валюты отсутствует в адресе (должен содержать 3 заглавные буквы A-Z)")));
                return;
            }

            String code = pathInfo.substring(1);

            Optional<Currency> currency = currencyDao.findByCode(code);
            if (currency.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(gson.toJson(Map.of("message", "Валюта не найдена")));
                return;
            }

            resp.getWriter().write(gson.toJson(currency.get()));
        }
    }
