package com.epam.java2021.library.dao.impl.mysql.util;

import com.epam.java2021.library.entity.Entity;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface EntityParser<T extends Entity> {
    T accept(ResultSet rs) throws SQLException;
}