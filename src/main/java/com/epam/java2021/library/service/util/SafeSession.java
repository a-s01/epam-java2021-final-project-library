package com.epam.java2021.library.service.util;

import javax.servlet.http.HttpSession;

public class SafeSession extends Safe<Object> {
    private final HttpSession session;

    public SafeSession(HttpSession session) {
        this.session = session;
    }

    @Override
    public Object get(String s) {
        return session.getAttribute(s);
    }

    @Override
    public String getStr(String s) {
        return (String) s;
    }
}
