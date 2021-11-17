package com.epam.java2021.library.dao.factory;

import com.epam.java2021.library.dao.factory.impl.DBDaoFactory;

public class DaoFactoryCreator {
    public static IDaoFactory getDefaultFactory() {
        return new DBDaoFactory();
    }
}
