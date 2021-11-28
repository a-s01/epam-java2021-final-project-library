package com.epam.java2021.library.testutil;

import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;

import java.sql.Connection;
import java.sql.SQLException;

public interface TestBody {
    void accept(Connection c) throws ServiceException, DaoException, SQLException;
}
