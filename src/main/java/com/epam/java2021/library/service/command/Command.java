package com.epam.java2021.library.service.command;

import com.epam.java2021.library.exception.AjaxException;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;

/**
 * Commands are used by Front controller to do business logic.
 */
public interface Command {
    /**
     * All command logic is done here.
     *
     * @param req user request
     * @return jsp (usually) page to be shown to user
     * @throws DaoException in case errors according to DAO
     * @throws ServiceException all other application logic errors
     * @throws AjaxException only in case of AJAX request, allows differentiating AJAX among others
     */
    String execute(HttpServletRequest req) throws DaoException, ServiceException, AjaxException;
}