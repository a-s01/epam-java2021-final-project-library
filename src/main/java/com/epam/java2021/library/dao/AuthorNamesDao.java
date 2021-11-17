package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.impl.AuthorNames;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;

public interface AuthorNamesDao extends AbstractDao<AuthorNames> {
    AuthorNames readByLandId(long id) throws DaoException, ServiceException;
}