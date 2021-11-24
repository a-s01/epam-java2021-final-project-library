package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.impl.Author;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;

import java.util.List;

public interface AuthorDao extends AbstractDao<Author>{
    List<Author> findByBookID(long id) throws DaoException, ServiceException;
}