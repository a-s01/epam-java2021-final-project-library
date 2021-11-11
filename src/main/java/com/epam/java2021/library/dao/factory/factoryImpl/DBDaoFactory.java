package com.epam.java2021.library.dao.factory.factoryImpl;

import com.epam.java2021.library.dao.factory.factoryImpl.db.MySQLDaoFactory;
import com.epam.java2021.library.dao.factory.IDaoFactory;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;

public class DBDaoFactory implements IDaoFactory {
    public IDaoFactoryImpl getDefaultImpl() {
        return new MySQLDaoFactory();
    }
}
