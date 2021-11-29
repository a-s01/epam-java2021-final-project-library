package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.LangDao;
import com.epam.java2021.library.entity.impl.Lang;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.testutil.DBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TestLangDao {
    private static final DBManager dbManager = DBManager.getInstance();

    @Before
    public void initTest() throws ServiceException, IOException, InterruptedException {
        dbManager.executeScript();
    }

    @Test
    public void testGetAll() throws ServiceException, SQLException, DaoException {
        dbManager.testWrapper(c -> {
            LangDao langDao = new LangDaoImpl(c);
            List<Lang> langs = langDao.getAll();
            Assert.assertTrue(langs.size() > 0);
        });
    }

    @Test
    public void testReadByID() throws ServiceException, SQLException, DaoException {
        dbManager.testWrapper(c -> {
            LangDao langDao = new LangDaoImpl(c);
            Lang en = langDao.read(1);
            Assert.assertEquals(new Lang.Builder().setCode("en").setId(1).build(), en);
        });
    }

    @Test
    public void testReadByName() throws ServiceException, SQLException, DaoException {
        dbManager.testWrapper(c -> {
            LangDao langDao = new LangDaoImpl(c);
            Lang en = langDao.read("en");
            Assert.assertEquals(new Lang.Builder().setCode("en").setId(1).build(), en);
        });
    }
}
