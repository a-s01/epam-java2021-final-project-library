package com.epam.java2021.library.exception;

public class UserException extends Exception {
    private String errorField;

    public UserException(String msg) {
        super(msg);
    }

    public UserException(String msg, String errorField) {
        super(msg);
        this.errorField = errorField;
    }

    public String getErrorField() {
        return errorField;
    }
}
