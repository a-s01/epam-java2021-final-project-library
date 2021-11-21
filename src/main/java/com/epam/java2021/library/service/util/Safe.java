package com.epam.java2021.library.service.util;

import com.epam.java2021.library.exception.ServiceException;

public abstract class Safe<K> {
    
    public abstract K get(String s);
    public abstract String getStr(String s);
    
    public  <V> V getNotNullParameter(String param, TypeConverter<K, V> converter) throws ServiceException {
        V v = getParameter(param, converter);
        if (v == null) {
            throw new ServiceException(param + " cannot be empty.");
        }
        return v;
    }

    public  <V> V getParameter(String param, TypeConverter<K, V> converter) {
        K k = get(param);
        if (k == null) {
            return null;
        }

        return converter.process(k);
    }
    
    public String getNotEmptyString(String param) throws ServiceException {
        String s = getStr(param);
        if (s == null || s.trim().equals("")) {
            throw new ServiceException(param + " cannot be empty.");
        }

        return s.trim();
    }

    public String getString(String param) {
        String s = getStr(param);
        if (s == null) {
            return "";
        }

        return s.trim();
    }
}
