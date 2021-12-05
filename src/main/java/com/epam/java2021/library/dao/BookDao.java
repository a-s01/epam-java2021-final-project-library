package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.exception.DaoException;

import java.util.List;

/**
 * Functions specific to Book class
 */
public interface BookDao extends AbstractSuperDao<Book> {
    List<Book> getBooksInBooking(long id) throws DaoException;
}