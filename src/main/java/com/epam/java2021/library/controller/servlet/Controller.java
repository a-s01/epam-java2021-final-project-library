package com.epam.java2021.library.controller.servlet;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.command.CommandContext;
import com.epam.java2021.library.service.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.epam.java2021.library.constant.ServletAttributes.ATTR_OUTPUT;
import static com.epam.java2021.library.constant.ServletAttributes.SERVICE_ERROR;

@WebServlet("/controller")
public class Controller extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(Controller.class);

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.debug("start");
        logger.trace("uri={}, query={}", req.getRequestURI(), req.getQueryString());


        String page = proceed(req, resp);
        if (page == null) {
            logger.debug("No page was returned, looking if it's ajax request");
            /*String plainText = (String) req.getAttribute(PLAIN_TEXT);

            if (plainText != null) {
                logger.debug("It's ajax, output result");
                logger.trace("plainText={}", plainText);
                resp.setContentType("text/plain");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(plainText);
                return;
            }*/

            String output = (String) req.getAttribute(ATTR_OUTPUT);
            if (output != null) {
                logger.debug("It's ajax, output result");
                /*String format = (String) req.getAttribute(ATTR_FORMAT);
                if (format == null) {*/
                    logger.trace("format is null, set to text/plain");
                    resp.setContentType("text/plain");
                /*} else {
                    logger.trace("format={}", format);
                    resp.setContentType(format);
                }*/
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(output);
                return;
            }
            page = redirectToError("no page was returned by command", req.getSession());
        }
        logger.debug("redirect to page={}", page);
        resp.sendRedirect(page);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("start");
        logger.trace("uri={},query={}", req.getRequestURI(), req.getQueryString());

        String page = proceed(req, resp);

        if (page == null) {
            page = redirectToError("no page was returned by command", req.getSession());
        }

        logger.debug("forward to page={}", page);
        req.getRequestDispatcher(page).forward(req, resp);
    }

    private String proceed(HttpServletRequest req, HttpServletResponse resp) {
        String commandStr = req.getParameter("command");
        logger.trace("commandStr = {}", commandStr);
        logger.trace("encoding={}", req.getCharacterEncoding());
        HttpSession session = req.getSession();

        session.removeAttribute(SERVICE_ERROR);

        try {
            Command command = CommandContext.getCommand(commandStr);
            return command.execute(session, req);
        } catch (DaoException | ServiceException e) {
            return redirectToError(e.getMessage(), session);
        }
    }

    private String redirectToError(String msg, HttpSession session) {
        logger.error(msg);
        session.setAttribute(SERVICE_ERROR, msg);
        return Pages.ERROR;
    }
}
