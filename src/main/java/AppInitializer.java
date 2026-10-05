import dao.CurrencyDao;
import dao.DatabaseInitializer;
import dao.ExchangeRateDao;
import dao.impl.CurrencyDaoImpl;
import dao.impl.ExchangeRateDaoImpl;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import service.CurrencyService;
import service.ExchangeRateService;
import service.ExchangeService;
import service.impl.CurrencyServiceImpl;
import service.impl.ExchangeRateServiceImpl;
import service.impl.ExchangeServiceImpl;

import java.util.logging.Logger;

@WebListener
public class AppInitializer implements ServletContextListener {

    private static final Logger log = Logger.getLogger(AppInitializer.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Initializing database...");
        DatabaseInitializer.init();
        log.info("Database initialized successfully!");

        CurrencyDao currencyDao = new CurrencyDaoImpl();
        ExchangeRateDao exchangeRateDao = new ExchangeRateDaoImpl();

        CurrencyService currencyService = new CurrencyServiceImpl(currencyDao);
        ExchangeRateService exchangeRateService = new ExchangeRateServiceImpl(exchangeRateDao, currencyDao);
        ExchangeService exchangeService = new ExchangeServiceImpl(exchangeRateDao, currencyDao);

        ServletContext context = sce.getServletContext();
        context.setAttribute("currencyService", currencyService);
        context.setAttribute("exchangeRateService", exchangeRateService);
        context.setAttribute("exchangeService", exchangeService);
    }
}
