package com.epam.java2021.library.dao.impl.mysql.func;

import com.epam.java2021.library.exception.DaoException;

import java.sql.Connection;

/**
 * Interface to be used in lambda-s in {@link com.epam.java2021.library.dao.impl.mysql.util.Transaction} class.
 * Changes something in DB, should be used only in transaction
 */
@FunctionalInterface
public interface DaoChanger {
    void proceed(Connection c) throws DaoException;
}