package com.epam.java2021.library.exception;

public class DaoException extends Exception {
    public DaoException(String msg) {
        super(msg);
    }

    public DaoException(String msg, Exception cause) {
        super(msg, cause);
    }
}
