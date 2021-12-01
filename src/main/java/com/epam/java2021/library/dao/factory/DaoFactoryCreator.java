package com.epam.java2021.library.dao.factory;

import com.epam.java2021.library.dao.factory.impl.DBDaoFactory;

public class DaoFactoryCreator {

    private DaoFactoryCreator() {}

    public static AbstractDaoFactory getDefaultFactory() {
        return new DBDaoFactory();
    }
}
