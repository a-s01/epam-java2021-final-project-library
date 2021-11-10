package com.epam.java2021.library.service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBManager {
    private static DataSource ds;
    private static DBManager INSTANCE;

    private DBManager(DataSource ds) {
        this.ds = ds;
    }

    public static synchronized DBManager getInstance(DataSource ds) {
        if (INSTANCE == null) {
            INSTANCE = new DBManager(ds);
        }
        return INSTANCE;
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
