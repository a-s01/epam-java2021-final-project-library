package com.epam.java2021.library.dao.factory.factoryImpl.db;

import com.epam.java2021.library.dao.*;
import com.epam.java2021.library.dao.daoImpl.mysql.*;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.entity.entityImpl.BookStat;
import com.epam.java2021.library.entity.entityImpl.Language;

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
        return new BookDaoImpl();
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
    public AbstractDao<Language> getLangDao() {
        return new LangDaoImpl();
    }

    @Override
    public AbstractDao<BookStat> getBookStatDao() {
        return new BookStatDaoImpl();
    }

    @Override
    public AuthorNamesDao getAuthorNamesDao() {
        return new AuthorNamesDaoImpl();
    }
}
