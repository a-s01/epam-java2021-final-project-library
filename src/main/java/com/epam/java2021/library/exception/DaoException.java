package com.epam.java2021.library.exception;

public class DaoException extends Exception {
    private static final long serialVersionUID = 1L;

    public DaoException(String msg) {
        super(msg);
    }

    public DaoException(String msg, Exception cause) {
        super(msg, cause);
    }
}
