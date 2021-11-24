package com.epam.java2021.library.dao.impl.mysql.util;

import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.exception.DaoException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface EntityParser<T extends Entity> {
    T accept(Connection c, ResultSet rs) throws SQLException, DaoException;
}