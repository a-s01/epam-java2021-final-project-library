package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;

import java.util.List;

public interface SuperDao<E extends Entity> extends AbstractDao<E> {
    List<E> findByPattern(String what, String searchBy, String sortBy, int num, int page)
            throws ServiceException, DaoException;
    int findByPatternCount(String what, String searchBy, String sortBy)
            throws ServiceException, DaoException;
    List<E> findBy(String what, String searchBy) throws ServiceException, DaoException; // TODO to delete
}