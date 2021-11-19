package com.epam.java2021.library.controller.listener;

import com.epam.java2021.library.constant.ServletAttributes;
import com.epam.java2021.library.entity.impl.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.List;

public class ContextListener implements ServletContextListener {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(ContextListener.class);

    public void contextDestroyed(ServletContextEvent event) {
        logger.debug("Servlet context destruction init...");
        // do nothing
        logger.debug("Servlet context destruction finished");
    }

    public void contextInitialized(ServletContextEvent event) {
        logger.debug("Servlet context initialization init...");

        ServletContext servletContext = event.getServletContext();
        initUserRoles(servletContext);
        initSupportedLangs(servletContext);

        logger.debug("Servlet context initialization finished");
    }

    private void initUserRoles(ServletContext servletContext) {
        logger.debug("init...");

        List<String> appRoles = new ArrayList<>();
        for (User.Role r: User.Role.values()) {
            if (r.equals(User.Role.UNKNOWN)) {
                continue;
            }
            appRoles.add(r.name().toLowerCase());
        }
        servletContext.setAttribute(ServletAttributes.APP_ROLES, appRoles);
        logger.trace("{}={}", ServletAttributes.APP_ROLES, appRoles);
        logger.debug("finished");
    }

    private void initSupportedLangs(ServletContext servletContext) {
    }
}
