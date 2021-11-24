package com.epam.java2021.library.dao.impl.mysql.util;

import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class Transaction {
    private static final ConnectionPool pool = ConnectionPool.getInstance();
    private static final Logger logger = LogManager.getLogger(Transaction.class);
    private final boolean close;
    private Connection conn;

    public Transaction(Connection c) throws DaoException {
        if (c == null) {
            logger.trace("Init connection from connection pool...");
            try {
                conn = pool.getConnection();
            } catch (SQLException e) {
                logAndThrow(e);
            }
            logger.trace("Got connection from connection pool: {}", conn);
            close = true;
        } else {
            logger.trace("Got connection from outside: {}", c);
            conn = c;
            close = false;
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public void initTransaction() throws DaoException {
        logger.trace("auto commit = false");
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            logAndThrow(e);
        }
    }

    public void close() throws DaoException {
        logger.trace("close connection: {}, close={}", conn, close);
        if (close) {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                logAndThrow(e);
            }
        }
    }

    private void logAndThrow(SQLException e) throws DaoException {
        logger.error(e.getMessage());
        throw new DaoException(e.getMessage(), e);
    }

    public void commit() throws DaoException {
        logger.trace("commit connection: {}", conn);
        if (close) {
            logger.trace("close is true, committing...");
            try {
                conn.commit();
            } catch (SQLException e) {
                logAndThrow(e);
            }
        } else {
            logger.trace("close is false, no commit yet");
        }
    }

    public void rollback() throws DaoException {
        logger.trace("rollback connection: {}", conn);
        if (close) {
            logger.trace("close is true, initiate rollback...");
            try {
                conn.rollback();
            } catch (SQLException e) {
                logAndThrow(e);
            }
        } else {
            logger.trace("close is false, no rollback yet");
        }
    }

    public void transactionWrapper(DaoChanger changer) throws DaoException {
        this.initTransaction();
        try {
            Connection c = this.getConnection();

            changer.proceed(c);

            this.commit();
        } catch (DaoException e) {
            this.rollback();
            throw e;
        } finally {
            this.close();
        }
    }

    public <T> T noTransactionWrapper(DaoReader<T> reader) throws DaoException {
        Connection c = this.getConnection();
        T t = reader.proceed(c);
        this.close();
        return t;
    }
}
