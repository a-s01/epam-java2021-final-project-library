package com.epam.java2021.library.controller.listener;

import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

public class ContextListener implements ServletContextListener {
    private static final Logger logger = LogManager.getLogger(ContextListener.class);
    /**
     *
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        try {
            Context initContext = new InitialContext();
            Context envContext  = (Context)initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/library-app");
            servletContext.setAttribute("ds", ds);
            logger.info("DataSource initialized");
        } catch (NamingException e) {
            logger.error("Unable to get DB connection pool: " + e.getMessage());
        }

        IDaoFactoryImpl daoFactory = DaoFactoryCreator.getDefaultFactory().getDefaultImpl();
        servletContext.setAttribute("daoFactory", daoFactory);
        logger.info("DaoFactory initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}