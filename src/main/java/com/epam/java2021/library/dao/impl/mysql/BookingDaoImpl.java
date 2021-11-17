package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.BookingDao;
import com.epam.java2021.library.entity.impl.Booking;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.Set;

public class BookingDaoImpl implements BookingDao {
    private static final Logger logger = LogManager.getLogger(BookingDaoImpl.class);
    private Connection conn;

    public BookingDaoImpl() {}
    public BookingDaoImpl(Connection conn) {}

    @Override
    public void create(Booking entity) throws DaoException, ServiceException {

    }

    @Override
    public Booking read(long id) throws DaoException, ServiceException {
        return null;
    }

    @Override
    public void update(Booking entity) throws DaoException, ServiceException {

    }

    @Override
    public void delete(Booking entity) throws DaoException, ServiceException {

    }

    @Override
    public void addBooks(Set<Long> bookIDs) {

    }
}
