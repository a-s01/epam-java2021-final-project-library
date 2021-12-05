package com.epam.java2021.library.dao.factory;

import com.epam.java2021.library.dao.factory.impl.DBDaoFactory;

/**
 * Abstract factory, returns factory of supported storage type: DB, XML, ...
 */
public class DaoFactoryCreator {

    private DaoFactoryCreator() {}

    public static AbstractDaoFactory getDefaultFactory() {
        return new DBDaoFactory();
    }
}
