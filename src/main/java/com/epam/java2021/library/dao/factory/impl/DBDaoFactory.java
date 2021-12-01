package com.epam.java2021.library.dao.factory.impl;

import com.epam.java2021.library.dao.factory.AbstractDaoFactory;
import com.epam.java2021.library.dao.factory.DaoFactoryImpl;
import com.epam.java2021.library.dao.factory.impl.db.MySQLDaoFactory;

public class DBDaoFactory implements AbstractDaoFactory {
    public DaoFactoryImpl newInstance() {
        return new MySQLDaoFactory();
    }
}
