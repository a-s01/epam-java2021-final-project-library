package com.epam.java2021.library.service.util;

import com.epam.java2021.library.exception.UserException;

import javax.servlet.http.HttpServletRequest;

public class SafeRequest extends Safe<String> {
    private final HttpServletRequest req;

    public SafeRequest(HttpServletRequest req) {
        this.req = req;
    }

    @Override
    public String get(String s) {
        return req.getParameter(s);
    }

    @Override
    public String getStr(String s) {
        return get(s);
    }
}
