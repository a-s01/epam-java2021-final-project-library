package com.epam.java2021.library.service.validator;

import com.epam.java2021.library.exception.ServiceException;

/**
 * Wrapper used to take info from HTTP request/session/context safely
 * @param <K> request/session/context
 */
public abstract class Safe<K> {
    protected K value;
    protected String param;
    public abstract Safe<K> get(String s);

    /**
     * For string conversion you must use notEmpty first
     * @param converter converts requested parameter from type K to type V
     * @param <V> type of expected result
     * @return result of type V
     * @throws ServiceException in case of inconsistency of requested parameter
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
