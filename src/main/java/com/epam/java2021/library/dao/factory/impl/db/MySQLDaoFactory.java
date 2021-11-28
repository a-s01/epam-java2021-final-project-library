package com.epam.java2021.library.dao.factory.impl.db;

import com.epam.java2021.library.dao.*;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.dao.impl.mysql.*;

public class MySQLDaoFactory implements IDaoFactoryImpl {

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