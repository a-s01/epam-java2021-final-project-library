package com.epam.java2021.library.controller.servlet;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.exception.AjaxException;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.command.Command;
import com.epam.java2021.library.service.command.CommandContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.epam.java2021.library.constant.ServletAttributes.SERVICE_ERROR;

@WebServlet("/controller")
public class Controller extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(Controller.class);

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("start");
        logger.trace("uri={}, query={}", req.getRequestURI(), req.getQueryString());

        String page;
        try {
            page = proceed(req);
        } catch (AjaxException e) {
            if (e.getNextPage() != null) {
                logger.debug("forward to page={}", e.getNextPage());
                req.getRequestDispatcher(e.getNextPage()).forward(req, resp);
                return;
            }
            responseAjaxError(req, resp, e);
            return;
        }

        logger.debug("redirect to page={}", page);
        resp.sendRedirect(page);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("start");
        logger.trace("uri={},query={}", req.getRequestURI(), req.getQueryString());

        String page;
        try {
            page = proceed(req);
        } catch (AjaxException e) {
            if (e.getNextPage() == null) {
                responseAjaxError(req, resp, e);
                return;
            }
            page = e.getNextPage();
        }

        logger.debug("forward to page={}", page);
        req.getRequestDispatcher(page).forward(req, resp);
    }

    private String proceed(HttpServletRequest req) throws AjaxException {
        String commandStr = req.getParameter("command");
        logger.trace("commandStr={}, encoding={}", commandStr, req.getCharacterEncoding());

        try {
            Command command = CommandContext.getCommand(commandStr);
            String page = command.execute(req);
            if (page == null) {
                throw new ServiceException("error.no.page.was.returned");
            }
            return page;
        } catch (DaoException | ServiceException e) {
            return redirectToError(e.getMessage(), req);
        }
    }

    private String redirectToError(String msg, HttpServletRequest req) {
        logger.error(msg);
        req.getSession().setAttribute(SERVICE_ERROR, msg);
        return Pages.ERROR;
    }

    private void responseAjaxError(HttpServletRequest req, HttpServletResponse resp, AjaxException e)
            throws IOException, ServletException {
        logger.error(e.getMessage());
        logger.trace("{}={}", SERVICE_ERROR, e.getMessage());

        resp.setStatus(e.getErrorCode());
        req.setAttribute(SERVICE_ERROR, e.getMessage());
        req.getRequestDispatcher(Pages.XML_SIMPLE_OUTPUT).forward(req, resp);
    }
}
