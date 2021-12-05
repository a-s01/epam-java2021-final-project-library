package com.epam.java2021.library.dao.impl.mysql.func;

import com.epam.java2021.library.exception.DaoException;

import java.sql.Connection;
/**
 * Interface to be used in lambda-s in {@link com.epam.java2021.library.dao.impl.mysql.util.Transaction} class.
 * Reads something from DB, should be used in non-transaction
 */
@FunctionalInterface
public interface DaoReader<T> {
    T proceed(Connection c) throws DaoException;
}