package com.epam.java2021.library.service.command;

import com.epam.java2021.library.exception.AjaxException;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface Command {
    String execute(HttpServletRequest req) throws DaoException, ServiceException, AjaxException;
}