package com.epam.java2021.library.dao.factory.impl;

import com.epam.java2021.library.dao.factory.IDaoFactory;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.dao.factory.impl.db.MySQLDaoFactory;

public class DBDaoFactory implements IDaoFactory {
    public IDaoFactoryImpl getDefaultImpl() {
        return new MySQLDaoFactory();
    }
}
