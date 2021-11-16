package com.epam.java2021.library.service;

import com.epam.java2021.library.dao.AbstractDao;
import com.epam.java2021.library.dao.AuthorDao;
import com.epam.java2021.library.dao.AuthorNamesDao;
import com.epam.java2021.library.dao.BookDao;
import com.epam.java2021.library.dao.daoImpl.mysql.EditRecordDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.entity.entityImpl.*;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookLogic {
    private static final Logger logger = LogManager.getLogger(BookLogic.class);
    private static final IDaoFactoryImpl daoFactory = DaoFactoryCreator.getDefaultFactory().getDefaultImpl();
    private BookLogic() {}

    public static List<Book> findBooks(String search, String searchBy, String sortBy, String num, String page) throws DaoException, ServiceException {
        logger.trace("findBooks request: query={}, searchBy={}, sortBy={}, num={}, page={}",
                search, searchBy, sortBy, num, page);
        BookDao bookDao = daoFactory.getBookDao();
        //AbstractDao<Language> langDao = daoFactory.getLangDao();
        AbstractDao<BookStat> bookStatDao = daoFactory.getBookStatDao();
        AuthorDao authorDao = daoFactory.getAuthorDao();
        //AuthorNamesDao authorNamesDao = daoFactory.getAuthorNamesDao();
        logger.trace("Created daos: bookDao={}, bookStatDao={}, authorDao={}",
                bookDao, bookStatDao, authorDao);

        List<Book> books = null;
        EntityTransaction transaction = new EntityTransaction();
        try {
            transaction.initTransaction(bookDao, bookStatDao, authorDao);
            books = bookDao.findByPattern(search, searchBy.toLowerCase(), sortBy.toLowerCase());
            if (books != null) {
                for (Book book : books) {
                    List<Author> authors = authorDao.findByBookID(book.getId());
                    book.setAuthors(authors);
                    BookStat bookStat = bookStatDao.read(book.getId());
                    book.setBookStat(bookStat);
                }
            }
            transaction.commit();
        } catch (DaoException | ServiceException e) {
            transaction.rollback();
            logger.error(e.getMessage());
            throw e;
        } finally {
            transaction.endTransaction();
        }
        return books;
    }
}
