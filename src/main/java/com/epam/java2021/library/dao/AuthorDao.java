package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.impl.Author;
import com.epam.java2021.library.exception.DaoException;

import java.util.List;

/**
 * Functions specific to Author class
 */
public interface AuthorDao extends AbstractSuperDao<Author> {
    List<Author> findByBookID(long id) throws DaoException;
    Author read(String name) throws DaoException;
    List<Author> findByPattern(String what) throws DaoException;
}