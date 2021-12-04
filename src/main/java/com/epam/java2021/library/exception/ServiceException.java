package com.epam.java2021.library.exception;

import java.util.ArrayList;
import java.util.List;

public class ServiceException extends Exception {
    private static final long serialVersionUID = 1L;
    private final List<String> msgParameters;

    public ServiceException(String msg, Throwable cause) {
        super(msg, cause);
        msgParameters = null;
    }

    public ServiceException(String msg) {
        super(msg);
        msgParameters = null;
    }

    public ServiceException(String msg, List<String> msgParameters) {
        super(msg);
        this.msgParameters = msgParameters;
    }

    public List<String> getMsgParameters() {
        if (msgParameters == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(msgParameters);
    }
}
