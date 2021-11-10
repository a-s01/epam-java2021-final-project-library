package com.epam.java2021.library.dao.factory.factoryImpl.db;

import com.epam.java2021.library.dao.AuthorDao;
import com.epam.java2021.library.dao.BookDao;
import com.epam.java2021.library.dao.BookingDao;
import com.epam.java2021.library.dao.UserDao;
import com.epam.java2021.library.dao.daoImpl.mysql.EditRecordDao;
import com.epam.java2021.library.dao.daoImpl.mysql.UserDaoImpl;
import com.epam.java2021.library.dao.factory.IDaoFactory;

import java.sql.Connection;

public class MySQLDaoFactory implements IDaoFactory {

    @Override
    public UserDao getUserDao(Connection conn) {
        return new UserDaoImpl(conn);
    }

    @Override
    public BookingDao getBookingDao(Connection conn) {
        return null;
    }

    @Override
    public BookDao getBookDao(Connection conn) {
        return null;
    }

    @Override
    public AuthorDao getAuthorDao(Connection conn) {
        return null;
    }

    @Override
    public EditRecordDao getEditRecordDao(Connection conn) {
        return new EditRecordDao(conn);
    }
}
