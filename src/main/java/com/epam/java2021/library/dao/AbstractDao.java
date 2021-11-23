package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;

public interface AbstractDao<E extends Entity> {
    void create(E entity) throws DaoException, ServiceException;
    E read(long id) throws DaoException, ServiceException;
    void update(E entity) throws DaoException, ServiceException;
    void delete(long id) throws DaoException, ServiceException;
}