package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.exception.DaoException;

/**
 * Common function for all entities
 * @param <E> sub-class of Entity
 */
public interface AbstractEntityDao<E extends Entity> {
    void create(E entity) throws DaoException;
    E read(long id) throws DaoException;
    void update(E entity) throws DaoException;
    void delete(long id) throws DaoException;
}