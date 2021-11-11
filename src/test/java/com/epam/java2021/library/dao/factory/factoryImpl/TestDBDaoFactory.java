package com.epam.java2021.library.dao.factory.factoryImpl;

import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import org.junit.Assert;
import org.junit.Test;

public class TestDBDaoFactory {
    @Test
    public void testGetDefaultImpl() {
        IDaoFactoryImpl factory = new DBDaoFactory().getDefaultImpl();
        Assert.assertNotNull(factory);
    }
}
