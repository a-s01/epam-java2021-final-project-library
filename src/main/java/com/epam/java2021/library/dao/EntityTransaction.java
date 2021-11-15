package com.epam.java2021.library.dao;

import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class EntityTransaction {
    private static final Logger logger = LogManager.getLogger(EntityTransaction.class);
    private Connection conn;

    public void initTransaction(AbstractDao<?> dao, AbstractDao<?>... daos) throws DaoException {
        logger.trace("Init transaction..");
        initConnection();

        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            logAndThrow("Unable to set autocommit to false", e);
        }

        dao.setConnection(conn);
        for (AbstractDao<?> d: daos) {
            d.setConnection(conn);
        }
    }

    private void initConnection() throws DaoException {
        if (conn == null) {
            try {
                conn = ConnectionPool.getInstance().getConnection();
            } catch (Exception e) {
                logAndThrow("Unable to get connection from pool", e);
            }
        }
    }

    public void init(AbstractDao<?> dao) throws DaoException {
        logger.trace("Init dao..");
        initConnection();
        dao.setConnection(conn);
    }

    public void endTransaction() throws DaoException {
        logger.trace("End transaction");
        try {
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            logAndThrow("Unable to set autocommit to true", e);
        }
        closeConnection();
    }

    public void end() throws DaoException {
        logger.trace("End single-dao transaction");
        closeConnection();
    }

    private void closeConnection() throws DaoException {
        if (conn == null) {
            return;
        }

        try {
            logger.trace("Return connection to the pool: {}", conn);
            conn.close();
        } catch (SQLException e) {
            logAndThrow("Unable to return connection to pool", e);
        }
        conn = null;
    }

    public void commit() throws DaoException {
        logger.trace("Commit transaction");

        final String msg = "Unable to commit transaction";
        if (conn == null) {
            logAndThrow(msg, new DaoException("connection is null"));
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            logAndThrow(msg, e);
        }
    }

    public void rollback() throws DaoException {
        logger.trace("Rollback transaction");

        try {
            conn.rollback();
        } catch (SQLException e) {
            logAndThrow("Unable to rollback transaction", e);
        }
    }

    private void logAndThrow(String msg, Exception e) throws DaoException {
        logger.error("{}: {}", msg, e.getMessage());
        throw new DaoException(msg, e);
    }
}
