package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.entityImpl.Book;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;

import java.time.Year;
import java.util.HashMap;
import java.util.List;

public interface BookDao extends EditableAbstractDao<Book>, ComplexType<Book> {
    //List<Book> find(String what, String searchBy, String sortBy);
    List<Book> findByPattern(String what, String searchBy, String sortBy) throws ServiceException, DaoException;
}
