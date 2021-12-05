package com.epam.java2021.library.dao.impl.mysql.func;

import com.epam.java2021.library.entity.Entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * Interface to be used in lambda-s in {@link com.epam.java2021.library.dao.impl.mysql.util.BaseDao} class.
 * Fills Prepared statement with data from Entity of given type
 */
@FunctionalInterface
public interface StatementFiller<T extends Entity> {
    int accept(T entity, PreparedStatement ps) throws SQLException;
}