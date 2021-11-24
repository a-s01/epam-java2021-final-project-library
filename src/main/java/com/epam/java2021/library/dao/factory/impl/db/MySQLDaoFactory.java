package com.epam.java2021.library.dao.factory.impl.db;

import com.epam.java2021.library.dao.*;
import com.epam.java2021.library.dao.impl.mysql.*;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.entity.impl.BookStat;
import com.epam.java2021.library.entity.impl.Lang;

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
        return new BookSuperDao();
    }

    @Override
    public AuthorDao getAuthorDao() {
        return new AuthorDaoImpl();
    }

    @Override
    public EditRecordDao getEditRecordDao() {
        return new EditRecordDao();
    }


    @Override
    public LangDao getLangDao() {
        return new LangDaoImpl();
    }

    @Override
    public AbstractDao<BookStat> getBookStatDao() {
        return new BookStatDaoImpl();
    }
/*
    @Override
    public AuthorNamesDao getAuthorNamesDao() {
        return new AuthorNamesDaoImpl();
    } */
}