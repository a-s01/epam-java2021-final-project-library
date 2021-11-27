package com.epam.java2021.library.service.util;

import com.epam.java2021.library.exception.ServiceException;

public abstract class Safe<K> {
    protected K value;
    protected String param;
    public abstract Safe<K> get(String s);

    /**
     * for string convertion you must use notEmpty first
     * @param converter
     * @param <V>
     * @return
     * @throws ServiceException
     */
    public <V> V convert(TypeConverter<K, V> converter) throws ServiceException {
        try {
            return converter.process(value);
        } catch (IllegalArgumentException e) {
            throw new ServiceException(param + " has wrong type: " + e.getMessage());
        }
    }

    public Safe<K> notNull() throws ServiceException {
        if (value == null) {
            throw new ServiceException(param + " cannot be empty.");
        }
        return this;
    }

    protected void setParam(String param) {
        this.param = param;
    }
}
