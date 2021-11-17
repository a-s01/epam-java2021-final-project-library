package com.epam.java2021.library.dao.impl.util;

import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.exception.DaoException;

import java.sql.Connection;

@FunctionalInterface
public interface DaoReader<T extends Entity> {
    T proceed(Connection c) throws DaoException;
}