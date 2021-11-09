package com.epam.java2021.library.dao;

import java.sql.SQLException;

public class DaoException extends Exception {
    public DaoException(String msg) {
        super(msg);
    }

    public DaoException(String msg, SQLException cause) {
        super(msg, cause);
    }
}
