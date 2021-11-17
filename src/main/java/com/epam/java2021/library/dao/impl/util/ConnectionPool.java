package com.epam.java2021.library.dao.impl.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    private static final String DB_NAME = "jdbc/library-app";
    private static final Logger logger = LogManager.getLogger(ConnectionPool.class);
    private static final ConnectionPool instance = new ConnectionPool();
    private static DataSource ds;
    static {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            ds = (DataSource) envContext.lookup(DB_NAME);
            logger.info("Connection pool initialized.");
        } catch (NamingException e) {
            logger.fatal("Unable to init database pool: {}", e.getMessage());
        }
    }

    private ConnectionPool() {}
    public static ConnectionPool getInstance() {
        return instance;
    }

    public Connection getConnection() throws SQLException {
        Connection conn = ds.getConnection();
        logger.trace("New connection created {}", conn);
        return conn;
    }
}
