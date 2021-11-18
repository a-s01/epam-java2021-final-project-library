package com.epam.java2021.library.service.util;

import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface Command {
    void execute(HttpSession session, HttpServletRequest req) throws DaoException, ServiceException;
}