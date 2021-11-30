package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.impl.Author;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;

import java.util.List;

public interface AuthorDao extends AbstractSuperDao<Author> {
    List<Author> findByBookID(long id) throws DaoException, ServiceException;
    Author read(String name) throws DaoException;
    List<Author> findByPattern(String what) throws DaoException;
}