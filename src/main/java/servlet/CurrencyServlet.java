    package servlet;

    import dao.CurrencyDao;
    import jakarta.servlet.annotation.WebServlet;
    import jakarta.servlet.http.HttpServlet;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import model.Currency;

    import java.io.IOException;
    import java.util.Optional;

    import static jakarta.servlet.http.HttpServletResponse.*;
    import static util.ResponseUtil.error;
    import static util.ResponseUtil.json;

    @WebServlet("/currency/*")
    public class CurrencyServlet extends HttpServlet {
        private final CurrencyDao currencyDao = new CurrencyDao();

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || !pathInfo.matches("/[A-Z]{3}")) {
                error(resp, SC_BAD_REQUEST,"Код валюты отсутствует в адресе (должен содержать 3 заглавные буквы A-Z)");
                return;
            }

            String code = pathInfo.substring(1);

            Optional<Currency> currency = currencyDao.findByCode(code);
            if (currency.isEmpty()) {
                error(resp, SC_NOT_FOUND, "Валюта не найдена");
                return;
            }

            json(resp, SC_OK, currency.get());
        }
    }
