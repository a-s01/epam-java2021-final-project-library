package com.epam.java2021.library.controller.servlet;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.CommandContext;
import com.epam.java2021.library.service.util.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.epam.java2021.library.constant.ServletAttributes.PAGE;
import static com.epam.java2021.library.constant.ServletAttributes.SERVICE_ERROR;

@WebServlet("/controller")
public class Controller extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(Controller.class);

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.debug("Post request start");

        String page = proceed(req);
        logger.debug("redirect to page={}", page);
        resp.sendRedirect(page);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Get request start");

        String page = proceed(req);
        logger.debug("forward to page={}", page);
        req.getRequestDispatcher(page).forward(req, resp);
    }

    private String proceed(HttpServletRequest req) {
        String commandStr = req.getParameter("command");
        logger.trace("commandStr = {}", commandStr);
        HttpSession session = req.getSession();

        session.removeAttribute(SERVICE_ERROR);

        try {
            Command command = CommandContext.getCommand(commandStr);
            command.execute(session, req);
        } catch (DaoException | ServiceException e) {
            return redirectToError(e.getMessage(), session);
        }

        String page = (String) req.getAttribute(PAGE);

        if (page == null) {
            logger.error("no page was returned by command");
        }

        return page;
    }

    private String redirectToError(String msg, HttpSession session) {
        logger.error(msg);
        session.setAttribute(SERVICE_ERROR, msg);
        return Pages.ERROR;
    }
}
