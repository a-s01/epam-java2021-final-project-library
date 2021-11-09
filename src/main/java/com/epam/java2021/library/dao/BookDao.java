package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.entityImpl.Book;
import java.time.Year;
import java.util.List;

public interface BookDao extends AbstractDao<Book> {
    List<Book> findByTitle(String title, boolean exactMatch) throws DaoException;
    List<Book> findByISBN(String title, boolean exactMatch) throws DaoException;
    List<Book> findByYear(Year year) throws DaoException;
}
