package com.epam.java2021.library.dao.factory;

import com.epam.java2021.library.dao.AuthorDao;
import com.epam.java2021.library.dao.BookDao;
import com.epam.java2021.library.dao.BookingDao;
import com.epam.java2021.library.dao.UserDao;
import com.epam.java2021.library.dao.daoImpl.mysql.EditRecordDao;

public interface IDaoFactoryImpl {
    UserDao getUserDao();
    BookingDao getBookingDao();
    BookDao getBookDao();
    AuthorDao getAuthorDao();
    EditRecordDao getEditRecordDao();
    //AbstractDao<BookStat> getBookStatDao();
}
