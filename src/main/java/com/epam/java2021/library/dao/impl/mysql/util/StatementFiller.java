package com.epam.java2021.library.dao.impl.mysql.util;

import com.epam.java2021.library.entity.Entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface StatementFiller<T extends Entity> {
    void accept(T entity, PreparedStatement ps) throws SQLException;
}