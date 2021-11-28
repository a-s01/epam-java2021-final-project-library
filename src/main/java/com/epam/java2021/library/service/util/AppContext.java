package com.epam.java2021.library.service.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AppContext {
    private final Map<String, String> map = new ConcurrentHashMap<>();
    private static final AppContext INSTANCE = new AppContext();

    private AppContext() {}

    public static AppContext getInstance() {
        return INSTANCE;
    }

    public void setParameter(String key, String value) {
        map.put(key, value);
    }

    public String getParameter(String key) {
        return map.get(key);
    }
}
