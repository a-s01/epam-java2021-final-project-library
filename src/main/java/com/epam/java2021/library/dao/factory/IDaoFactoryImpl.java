package com.epam.java2021.library.dao.factory;

import com.epam.java2021.library.dao.*;
import com.epam.java2021.library.dao.impl.mysql.EditRecordDao;
import com.epam.java2021.library.entity.impl.*;

public interface IDaoFactoryImpl {
    UserDao getUserDao();
    BookingDao getBookingDao();
    SuperDao<Book> getBookDao();
    AuthorDao getAuthorDao();
    EditRecordDao getEditRecordDao();
    AbstractDao<Language> getLangDao();
    AbstractDao<BookStat> getBookStatDao();
    //AuthorNamesDao getAuthorNamesDao();
}
