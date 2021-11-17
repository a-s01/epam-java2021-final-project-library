package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.UserDao;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.testutil.DBManager;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TestUserDaoImpl {
    private static final Logger logger = LogManager.getLogger("TEST");
    private static final DBManager dbManager = DBManager.getInstance();
    public static final String TABLE = "user";
    public static final String EMAIL_COLUMN = "email";
    public static final String NAME_COLUMN = "name";
    public static final long ID = 99;
    public static final String EMAIL = "test@test.com";
    private static UserDao userDao;
    private static User user;
    private static Connection conn;
    private static String CREATE_USER;
    
    @BeforeClass
    public static void initTest() {
        final String pass = "123";
        final String salt = "123";
        final String coma = ", ";
        final String escape = "'";
        final String insertQuery = "INSERT INTO user VALUES (";
        try {
            conn = dbManager.getConnection();
        } catch (SQLException e) {
            logger.fatal("Cannot obtain database connection: " + e.getMessage());
        }
        userDao = DaoFactoryCreator.getDefaultFactory().getDefaultImpl().getUserDao();

        User.Builder builder = new User.Builder();
        builder.setEmail(EMAIL);
        builder.setPassword(pass);
        builder.setSalt(salt);
        user = builder.build();

        CREATE_USER = insertQuery + ID + coma +
                escape + EMAIL + escape + coma +
                escape + pass + escape + coma +
                escape + salt + escape + coma +
                "DEFAULT, DEFAULT, DEFAULT, NULL, DEFAULT, NULL)";
    }

    @Test(expected = DaoException.class)
    public void testCreateShouldThrowException() throws DaoException, ServiceException {
        User.Builder builder = new User.Builder();
        User user = builder.build();
        userDao.create(user);
    }

    @After
    public void clear() throws SQLException {
        dbManager.delete(conn, TABLE, EMAIL_COLUMN, user.getEmail());
    }
    
    @Test
    public void testCreateShouldCreateNewUserInDB() throws DaoException, SQLException, ServiceException {
        userDao.create(user);
        Assert.assertTrue(dbManager.read(conn, TABLE, EMAIL_COLUMN, user.getEmail()));
    }

    @Test
    public void testCreateShouldSetUserID() throws DaoException, ServiceException {
        userDao.create(user);
        Assert.assertNotEquals(-1, user.getId());
    }

    @Test
    public void testUpdateUserName() throws DaoException, SQLException, ServiceException {
        final String name = "test";
        userDao.create(user);
        user.setName(name);
        userDao.update(user);
        Assert.assertEquals(name, dbManager.readField(conn, NAME_COLUMN, TABLE, EMAIL_COLUMN, user.getEmail()));
    }

    @Test
    public void testReadUser() throws SQLException, DaoException, ServiceException {
        dbManager.execute(conn, CREATE_USER);
        User test = userDao.read(ID);
        Assert.assertEquals(test, user);
    }

    @Test
    public void testDeleteUser() throws DaoException, SQLException, ServiceException {
        userDao.delete(user);
        Assert.assertFalse(dbManager.read(conn, TABLE, EMAIL_COLUMN, user.getEmail()));
    }

    @Test
    public void testGetUserByEmail() throws SQLException, DaoException {
        dbManager.execute(conn, CREATE_USER);
        User test = userDao.findByEmail(EMAIL);
        Assert.assertEquals(user, test);
    }
/*
    @Test
    public void testGetUserByEmailPattern() throws SQLException, DaoException {
        dbManager.execute(conn, CREATE_USER);
        List<User> testList = userDao.findByEmailPattern(EMAIL.substring(1));
        List<User> users = new ArrayList<>();
        users.add(user);
        Assert.assertEquals(users, testList);
    } */

    /*

    @Test
    public void testGetRecords() throws DaoException {
        List<User> users = userDao.getRecords(0, 3);
        Assert.assertTrue(users.size() == 3);
    } */
    @AfterClass
    public static void cleanUp() throws SQLException {
        conn.close();
    }
}
