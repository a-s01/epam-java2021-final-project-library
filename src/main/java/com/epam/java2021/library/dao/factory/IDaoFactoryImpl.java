package com.epam.java2021.library.dao.factory;

import com.epam.java2021.library.dao.*;
import com.epam.java2021.library.dao.daoImpl.mysql.EditRecordDao;
import com.epam.java2021.library.entity.entityImpl.*;

public interface IDaoFactoryImpl {
    UserDao getUserDao();
    BookingDao getBookingDao();
    BookDao getBookDao();
    AuthorDao getAuthorDao();
    EditRecordDao getEditRecordDao();
    AbstractDao<Language> getLangDao();
    AbstractDao<BookStat> getBookStatDao();
    AuthorNamesDao getAuthorNamesDao();
}
