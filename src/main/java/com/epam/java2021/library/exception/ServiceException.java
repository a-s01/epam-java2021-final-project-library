package com.epam.java2021.library.exception;

public class ServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    public ServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ServiceException(String msg) {
        super(msg);
    }
}
