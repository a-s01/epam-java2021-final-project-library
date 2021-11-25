package com.epam.java2021.library.controller.listener;

import static com.epam.java2021.library.constant.ServletAttributes.*;
import com.epam.java2021.library.dao.LangDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.entity.impl.Lang;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.service.TaskScheduler;
import com.epam.java2021.library.service.util.UpdateFineTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.List;

public class ContextListener implements ServletContextListener {
    private static final Logger logger = LogManager.getLogger(ContextListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.debug("Servlet context destruction init...");
        // do nothing
        logger.debug("Servlet context destruction finished");
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger.debug("Servlet context initialization init...");

        ServletContext servletContext = event.getServletContext();
        initUserRoles(servletContext);
        initSupportedLanguages(servletContext);
        initScheduledTasks();

        logger.debug("Servlet context initialization finished");
    }

    private void initUserRoles(ServletContext servletContext) {
        logger.debug(START_MSG);

        List<String> appRoles = new ArrayList<>();
        for (User.Role r: User.Role.values()) {
            if (r.equals(User.Role.UNKNOWN)) {
                continue;
            }
            appRoles.add(r.name().toLowerCase());
        }
        servletContext.setAttribute(APP_ROLES, appRoles);
        logger.trace("{}={}", APP_ROLES, appRoles);
        logger.debug("finished");
    }

    private void initSupportedLanguages(ServletContext servletContext) {
        logger.debug(START_MSG);

        LangDao dao = DaoFactoryCreator.getDefaultFactory().getDefaultImpl().getLangDao();
        try {
            List<Lang> list = dao.getAll();
            servletContext.setAttribute(SUPPORTED_LANGUAGES, list);
            logger.info("Supported languages initialized.");
            logger.trace("{}={}", SUPPORTED_LANGUAGES, list);
        } catch (DaoException e) {
            logger.error("Unable to load supported languages: {}", e.getMessage());
        }
        logger.debug(END_MSG);
    }

    private void initScheduledTasks() {
        TaskScheduler scheduler = TaskScheduler.getInstance();
        scheduler.proceed(new UpdateFineTask(), 3600_000); // every one hour
    }
}
