package com.epam.java2021.library.dao.factory.impl.db;

import com.epam.java2021.library.dao.*;
import com.epam.java2021.library.dao.factory.DaoFactoryImpl;
import com.epam.java2021.library.dao.impl.mysql.*;

public class MySQLDaoFactory implements DaoFactoryImpl {

    @Override
    public UserDao getUserDao() {
        return new UserDaoImpl();
    }

    @Override
    public BookingDao getBookingDao() {
        return new BookingDaoImpl();
    }

    @Override
    public BookDao getBookDao() {
        return new BookDaoImpl();
    }

    @Override
    public AuthorDao getAuthorDao() {
        return new AuthorDaoImpl();
    }

    @Override
    public LangDao getLangDao() {
        return new LangDaoImpl();
    }

}