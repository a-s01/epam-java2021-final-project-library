package com.epam.java2021.library.dao.factory;

import com.epam.java2021.library.dao.*;
import com.epam.java2021.library.dao.daoImpl.mysql.EditRecordDao;

import java.sql.Connection;

public interface IDaoFactory {
    UserDao getUserDao(Connection conn);
    BookingDao getBookingDao(Connection conn);
    BookDao getBookDao(Connection conn);
    AuthorDao getAuthorDao(Connection conn);
    EditRecordDao getEditRecordDao(Connection conn);
    //AbstractDao<BookStat> getBookStatDao(Connection conn);
}
