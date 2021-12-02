package com.epam.java2021.library.exception;

public class AjaxException extends Exception {
    private static final long serialVersionUID = 1L;
    private int errorCode;
    private String nextPage;

    public AjaxException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public AjaxException(String nextPage) {
        this.nextPage = nextPage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getNextPage() {
        return nextPage;
    }
}
