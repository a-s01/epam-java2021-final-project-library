package com.epam.java2021.library.dao.impl.util;

import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.exception.DaoException;

import java.sql.Connection;
import java.util.List;

@FunctionalInterface
public interface DaoListReader<T extends Entity> {
    List<T> proceed(Connection c) throws DaoException;
}