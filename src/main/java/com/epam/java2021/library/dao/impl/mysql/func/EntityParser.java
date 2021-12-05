package com.epam.java2021.library.dao.impl.mysql.func;

import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.exception.DaoException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Interface to be used in lambda-s in {@link com.epam.java2021.library.dao.impl.mysql.util.BaseDao} class.
 * Parses Result Set to Entity of given type
 */
@FunctionalInterface
public interface EntityParser<T extends Entity> {
    T accept(Connection c, ResultSet rs) throws SQLException, DaoException;
}