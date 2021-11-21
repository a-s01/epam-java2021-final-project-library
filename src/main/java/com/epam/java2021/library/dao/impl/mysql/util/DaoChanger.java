package com.epam.java2021.library.dao.impl.mysql.util;

import com.epam.java2021.library.exception.DaoException;

import java.sql.Connection;

@FunctionalInterface
public interface DaoChanger {
    void proceed(Connection c) throws DaoException;
}