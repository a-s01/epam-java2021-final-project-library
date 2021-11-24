package com.epam.java2021.library.dao.factory;

import com.epam.java2021.library.dao.*;
import com.epam.java2021.library.dao.impl.mysql.EditRecordDao;
import com.epam.java2021.library.entity.impl.*;

public interface IDaoFactoryImpl {
    UserDao getUserDao();
    BookingDao getBookingDao();
    BookDao getBookDao();
    AuthorDao getAuthorDao();
    EditRecordDao getEditRecordDao();
    AbstractDao<Lang> getLangDao();
    AbstractDao<BookStat> getBookStatDao();
    //AuthorNamesDao getAuthorNamesDao();
}
