package com.epam.java2021.library.controller.listener;

import com.epam.java2021.library.dao.factory.DaoFactory;
import com.epam.java2021.library.dao.factory.DaoFactoryImpl;
import com.epam.java2021.library.dao.factory.IDaoFactory;
import com.epam.java2021.library.service.DBManager;
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
        try {
            ServletContext servletContext = event.getServletContext();
            Context initContext = new InitialContext();
            Context envContext  = (Context)initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/library-app");
            DBManager dbManager = DBManager.getInstance(ds);
            servletContext.setAttribute("dbManager", dbManager);
            logger.info("DataSource initialized");
            IDaoFactory daoFactory = DaoFactory.getDefaultFactory().getDefaultImpl();
            servletContext.setAttribute("daoFactory", daoFactory);
        } catch (NamingException e) {
            logger.error("Unable to get DB connection pool: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}