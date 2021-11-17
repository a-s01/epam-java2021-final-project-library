package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.BookDao;
import com.epam.java2021.library.dao.SuperDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.factory.IDaoFactory;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.testutil.DBManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TestBookDaoImpl {
    private static DBManager dbManager = DBManager.getInstance();
    private SuperDao<Book> bookDao;
    private Connection conn;

    @Before
    public void initDBConnection() throws SQLException {
        conn = dbManager.getConnection();
        bookDao = new BookSuperDao(conn);
    }

    private Book createBook(String title, String isbn, int year) {
        Book.Builder b = new Book.Builder();
        b.setTitle(title);
        b.setIsbn(isbn);
        b.setYear(year);
        return b.build();
    }

    @Test
    public void testFindByTitlePatternShouldReturnListOfOneBook() throws ServiceException, DaoException {
        List<Book> books = bookDao.findByPattern("миля", "title", "title", 5, 0);
        Assert.assertTrue(books.size() == 1);
        Book expected = createBook("Зеленая миля", "978-5-17-118362-2", 2020);
        Assert.assertEquals(expected, books.get(0));
    }

    @Test
    public void testFindByTitlePatternShouldReturnListOfExactOneBook() throws ServiceException, DaoException {
        List<Book> books = bookDao.findByPattern("миля", "title", "title", 5, 0);
        Book expected = createBook("Зеленая миля", "978-5-17-118362-2", 2020);
        Assert.assertEquals(expected, books.get(0));
    }

    @Test
    public void testFindByTitlePatternShouldReturnEmptyList() throws ServiceException, DaoException {
        List<Book> books = bookDao.findByPattern("sjkldfj;adskjf;asdjf", "title", "title", 5, 0);
        Assert.assertTrue(books.size() == 0);
    }

    @Test
    public void testFindByISBNPatternShouldReturnListOfOneBook() throws ServiceException, DaoException {
        List<Book> books = bookDao.findByPattern("978-5-17-118362-2", "isbn", "isbn", 5, 0);
        Assert.assertTrue(books.size() == 1);
    }

    @Test
    public void testFindByISBNPatternShouldReturnEmptyList() throws ServiceException, DaoException {
        List<Book> books = bookDao.findByPattern("11111111111111111", "isbn", "isbn", 5, 0);
        Assert.assertTrue(books.size() == 0);
    }

    @Test
    public void testFindByAuthorPatternShouldReturnEmptyList() throws ServiceException, DaoException {
        List<Book> books = bookDao.findByPattern("sjkldfj;adskjf;asdjf", "author", "title", 5, 0);
        Assert.assertTrue(books.size() == 0);
    }

    @Test
    public void testFindByAuthorPatternShouldReturnListOfOneBook() throws ServiceException, DaoException {
        List<Book> books = bookDao.findByPattern("Джоан", "author", "title", 5, 0);
        Assert.assertTrue(books.size() == 1);
    }

    @Test
    public void testFindByAuthorPatternShouldReturnListOfTwoBook() throws ServiceException, DaoException {
        List<Book> books = bookDao.findByPattern("Марио", "author", "title", 5, 0);
        Assert.assertTrue(books.size() == 2);
    }

    @After
    public void closeConnection() throws SQLException {
        conn.close();
    }
}
