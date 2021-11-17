package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.AuthorDao;
import com.epam.java2021.library.entity.impl.Author;
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

public class TestAuthorDaoImpl {
    private static final DBManager dbManager = DBManager.getInstance();
    private AuthorDao authorDao;
    private Connection conn;

    @Before
    public void initTest() throws SQLException {
        conn = dbManager.getConnection();
        authorDao = new AuthorDaoImpl(conn);
    }

    @Test
    public void testFindByBookIDShouldReturnListOfOneAuthor() throws ServiceException, DaoException {
        List<Author> authors = authorDao.findByBookID(40);
        Assert.assertTrue(authors.size() == 1);
    }

    private Author createAuthor(String name) {
        Author.Builder b = new Author.Builder();
        b.setName(name);
        return b.build();
    }

    @Test
    public void testFindByBookIDShouldReturnListOfOneExactAuthor() throws ServiceException, DaoException {
        List<Author> authors = authorDao.findByBookID(40);
        Author expected = createAuthor("Barnet Schecter");
        Assert.assertEquals(expected, authors.get(0));
    }

    @After
    public void closeConnection() throws SQLException {
        conn.close();
    }
}
