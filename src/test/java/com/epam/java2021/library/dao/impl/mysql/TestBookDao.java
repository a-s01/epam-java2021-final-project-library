package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.AbstractSuperDao;
import com.epam.java2021.library.dao.BookDao;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.testutil.DBManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TestBookDao {
    public static final String TITLE = "Гарри Поттер. Полное собрание (комплект из 7 книг) (сборник)";
    public static final String ISBN = "978-5-389-10668-0";
    public static final int YEAR = 2016;
    public static final String COLUMN = "title";
    public static final String BAD_INPUT = "sjkldfj;adskjf;asdjf";
    public static final String ISBN_COLUMN = "isbn";
    public static final int NUM = 5;
    public static final int PAGE = 1;
    private static final String AUTHOR_COLUMN = "author";
    private static DBManager dbManager = DBManager.getInstance();

    @Before
    public void initDBConnection() throws ServiceException, IOException, InterruptedException {
        dbManager.executeScript();
    }

    private Book createBook(String title, String isbn, int year) {
        Book.Builder b = new Book.Builder();
        b.setTitle(title);
        b.setIsbn(isbn);
        b.setYear(year);
        return b.build();
    }

    @Test
    public void testFindByTitlePatternShouldReturnListOfExactOneBook() throws ServiceException, DaoException, SQLException {
        dbManager.testWrapper(c -> {
            BookDao bookDao = new BookDaoImpl(c);
            List<Book> books = bookDao.findByPattern(TITLE,
                    COLUMN, COLUMN, 5, 1);
            Assert.assertTrue(books.size() == 1);
            Book expected = createBook(TITLE, ISBN, YEAR);
            Assert.assertEquals(expected, books.get(0));
        });
    }

    @Test
    public void testFindByTitlePatternShouldReturnEmptyList() throws ServiceException, DaoException, SQLException {
        dbManager.testWrapper(c -> {
            BookDao bookDao = new BookDaoImpl(c);
            List<Book> books = bookDao.findByPattern(BAD_INPUT, COLUMN, COLUMN, NUM, PAGE);
            Assert.assertEquals(0, books.size());
        });
    }

    @Test
    public void testFindByISBNPatternShouldReturnListOfOneBook() throws ServiceException, DaoException, SQLException {
        dbManager.testWrapper(c -> {
            BookDao bookDao = new BookDaoImpl(c);
            List<Book> books = bookDao.findByPattern(ISBN, ISBN_COLUMN, ISBN_COLUMN, NUM, PAGE);
            Assert.assertTrue(books.size() == 1);
        });
    }

    @Test
    public void testFindByISBNPatternShouldReturnEmptyList() throws ServiceException, DaoException, SQLException {
        dbManager.testWrapper(c -> {
            BookDao bookDao = new BookDaoImpl(c);
            List<Book> books = bookDao.findByPattern("11111111111111111", ISBN_COLUMN, ISBN_COLUMN, NUM, PAGE);
            Assert.assertEquals(0, books.size());
        });
    }

    @Test
    public void testFindByAuthorPatternShouldReturnEmptyList() throws ServiceException, DaoException, SQLException {
        dbManager.testWrapper(c -> {
            BookDao bookDao = new BookDaoImpl(c);
            List<Book> books = bookDao.findByPattern(BAD_INPUT, AUTHOR_COLUMN, COLUMN, NUM, PAGE);
            Assert.assertEquals(0, books.size());
        });
    }

    @Test
    public void testFindByAuthorPatternShouldReturnListOfOneBook() throws ServiceException, DaoException, SQLException {
        dbManager.testWrapper(c -> {
            BookDao bookDao = new BookDaoImpl(c);
            List<Book> books = bookDao.findByPattern("Джоан", AUTHOR_COLUMN, COLUMN, NUM, PAGE);
            Assert.assertEquals(1, books.size());
        });
    }

    @Test
    public void testFindByAuthorPatternShouldReturnListOfTwoBook() throws ServiceException, DaoException, SQLException {
        dbManager.testWrapper(c -> {
            BookDao bookDao = new BookDaoImpl(c);
            List<Book> books = bookDao.findByPattern("Марио", AUTHOR_COLUMN, COLUMN, NUM, PAGE);
            Assert.assertEquals(2, books.size());
        });
    }
}
