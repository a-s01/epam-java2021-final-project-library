package com.epam.java2021.library.dao.impl.mysql.util;

import com.epam.java2021.library.exception.DaoException;

import java.sql.Connection;

@FunctionalInterface
public interface DaoReader<T> {
    T proceed(Connection c) throws DaoException;
}