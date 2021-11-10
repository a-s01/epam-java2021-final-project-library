package com.epam.java2021.library.dao.factory;

import com.epam.java2021.library.dao.factory.factoryImpl.DBDaoFactory;

public class DaoFactory {
    public static DaoFactoryImpl getDefaultFactory() {
        return new DBDaoFactory();
    }
}
