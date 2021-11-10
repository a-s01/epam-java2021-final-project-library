package com.epam.java2021.library.dao.factory.factoryImpl;

import com.epam.java2021.library.dao.factory.factoryImpl.db.MySQLDaoFactory;
import com.epam.java2021.library.dao.factory.DaoFactoryImpl;
import com.epam.java2021.library.dao.factory.IDaoFactory;

public class DBDaoFactory implements DaoFactoryImpl {
    public IDaoFactory getDefaultImpl() {
        return new MySQLDaoFactory();
    }
}
