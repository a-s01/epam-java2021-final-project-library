package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;

import java.util.List;

/**
 * Common functions for some entities, which suppose to be searched by pattern with pagination
 * @param <E> sub-class of Entity
 */
public interface AbstractSuperDao<E extends Entity> extends AbstractEntityDao<E> {
    List<E> findByPattern(String what, String searchBy, String sortBy, int num, int page)
            throws ServiceException, DaoException;
    int findByPatternCount(String what, String searchBy)
            throws ServiceException, DaoException;
    List<E> findBy(String what, String searchBy) throws ServiceException, DaoException;
}