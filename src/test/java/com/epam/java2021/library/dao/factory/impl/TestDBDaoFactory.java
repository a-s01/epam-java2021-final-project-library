package com.epam.java2021.library.dao.factory.impl;

import com.epam.java2021.library.dao.factory.DaoFactoryImpl;
import org.junit.Assert;
import org.junit.Test;

public class TestDBDaoFactory {
    @Test
    public void testGetDefaultImpl() {
        DaoFactoryImpl factory = new DBDaoFactory().newInstance();
        Assert.assertNotNull(factory);
    }
}
