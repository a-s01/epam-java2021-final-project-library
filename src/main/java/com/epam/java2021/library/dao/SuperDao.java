package com.epam.java2021.library.dao;

import com.epam.java2021.library.dao.AbstractDao;
import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;

import java.util.List;

public interface SuperDao<E extends Entity> extends AbstractDao<E> {
    List<E> findByPattern(String what, String searchBy, String sortBy, int num, int page)
            throws ServiceException, DaoException;
}