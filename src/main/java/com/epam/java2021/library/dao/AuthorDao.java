package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.entityImpl.Author;
import com.epam.java2021.library.exception.DaoException;

import java.util.List;

public interface AuthorDao extends AbstractDao<Author>{
    List<Author> findByName(String name, boolean exactMatch) throws DaoException;
}
