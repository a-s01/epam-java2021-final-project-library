package com.epam.java2021.library.service.util;

import com.epam.java2021.library.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;

public class SafeRequest extends Safe<String> {
    private final HttpServletRequest req;

    public SafeRequest(HttpServletRequest req) {
        this.req = req;
    }

    @Override
    public SafeRequest get(String s) {
        value = req.getParameter(s);
        value = value == null ? "" : value.trim();
        setParam(s);

        return this;
    }
    
    public String convert() {
        return value;
    }

    public SafeRequest notEmpty() throws ServiceException {
        if (value.equals("")) {
            throw new ServiceException(param + " cannot be empty.");
        }

        return this;
    }

    public SafeRequest asEmail() throws ServiceException {
        // ^[\\p{L}!#$%&’*+/=?`{|}~^-]+(?:\\\\.[\\p{L}!#$%&’*+/=?`{|}~^-]+)*@(?:[\\p{L}-]+\\\\.)" +
        //                "+[\\p{L}]{2,6}$

        //^[\p{LD}\.-_*]+@[\p{LD}\\.-_]+\\.[\p{L}]{2,6}$
        if (!value.matches("^[\\p{LD}\\\\._-]+@[\\p{LD}\\\\._-]+\\.[\\p{L}]{2,6}$")) {
            throw new ServiceException(param + " should be valid email");
        }
        return this;
    }

    public SafeRequest escape() {
        value = value.replaceAll("([<>\\*\\+\\?^\\$\\{\\}\\(\\)\\|\\[\\]\\\\])", "\\\\$1");
        return this;
    }
}
