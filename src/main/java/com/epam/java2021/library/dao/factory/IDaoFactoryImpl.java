package com.epam.java2021.library.dao.factory;

import com.epam.java2021.library.dao.*;

public interface IDaoFactoryImpl {
    UserDao getUserDao();
    BookingDao getBookingDao();
    BookDao getBookDao();
    AuthorDao getAuthorDao();
    LangDao getLangDao();
}
