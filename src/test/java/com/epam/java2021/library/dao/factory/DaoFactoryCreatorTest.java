package com.epam.java2021.library.dao.factory;

import org.junit.Assert;
import org.junit.Test;

public class DaoFactoryCreatorTest {
    @Test
    public void testGetDefaultFactory() {
        IDaoFactory factory = DaoFactoryCreator.getDefaultFactory();
        Assert.assertNotNull(factory);
    }
}