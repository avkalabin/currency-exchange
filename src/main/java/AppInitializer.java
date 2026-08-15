import dao.DatabaseInitializer;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.logging.Logger;

@WebListener
public class AppInitializer implements ServletContextListener {

    private static final Logger log = Logger.getLogger(AppInitializer.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Initializing database...");
        DatabaseInitializer.init();
        log.info("Database initialized successfully!");
    }
}
