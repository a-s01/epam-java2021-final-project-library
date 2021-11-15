package com.epam.java2021.library.dao.factory.factoryImpl.db;

import com.epam.java2021.library.dao.AuthorDao;
import com.epam.java2021.library.dao.BookDao;
import com.epam.java2021.library.dao.BookingDao;
import com.epam.java2021.library.dao.UserDao;
import com.epam.java2021.library.dao.daoImpl.mysql.EditRecordDao;
import com.epam.java2021.library.dao.daoImpl.mysql.UserDaoImpl;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;

import java.sql.Connection;

public class MySQLDaoFactory implements IDaoFactoryImpl {

    @Override
    public UserDao getUserDao() {
        return new UserDaoImpl();
    }

    @Override
    public BookingDao getBookingDao() {
        return null;
    }

    @Override
    public BookDao getBookDao() {
        return null;
    }

    @Override
    public AuthorDao getAuthorDao() {
        return null;
    }

    @Override
    public EditRecordDao getEditRecordDao() {
        return new EditRecordDao();
    }
}
