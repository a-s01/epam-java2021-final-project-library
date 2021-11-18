package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import java.util.List;

public interface BookDao extends SuperDao<Book>{
    List<Book> getBooksInBooking(long id) throws DaoException;
    List<Book> findByPattern(String what, String searchBy, String sortBy, int num, int page) throws ServiceException, DaoException;
}