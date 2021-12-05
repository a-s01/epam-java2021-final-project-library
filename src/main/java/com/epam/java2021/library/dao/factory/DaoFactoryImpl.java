package com.epam.java2021.library.dao.factory;

import com.epam.java2021.library.dao.*;

/**
 * Concrete factory interface
 */
public interface DaoFactoryImpl {
    UserDao getUserDao();
    BookingDao getBookingDao();
    BookDao getBookDao();
    AuthorDao getAuthorDao();
    LangDao getLangDao();
}
