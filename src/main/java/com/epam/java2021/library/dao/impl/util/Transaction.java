package com.epam.java2021.library.dao.impl.util;

import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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

        try {
            conn.commit();
        } catch (SQLException e) {
            logAndThrow(e);
        }
    }

    public void rollback() throws DaoException {
        logger.trace("rollback connection: {}", conn);

        try {
            conn.rollback();
        } catch (SQLException e) {
            logAndThrow(e);
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

    public <T extends Entity> T noTransactionWrapper(DaoReader<T> reader) throws DaoException {
        Connection c = this.getConnection();
        T t = (T) reader.proceed(c);
        this.close();
        return t;
    }

    public <T extends Entity> List<T> noTransactionWrapperList(DaoListReader<T> reader) throws DaoException {
        Connection c = this.getConnection();
        List<T> list = (List<T>) reader.proceed(c);
        this.close();
        return list;
    }
}
