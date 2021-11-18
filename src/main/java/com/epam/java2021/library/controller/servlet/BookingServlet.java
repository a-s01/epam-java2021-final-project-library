package com.epam.java2021.library.controller.servlet;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.constant.ServletAttributes;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.BookingLogic;
import com.epam.java2021.library.service.util.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/booking")
public class BookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(BookingServlet.class);
    private Map<String, Command> commands;
    @Override
    public void init() {
        /*
                    booking?c=addBook...
                    * booking?c=removeBook
                    * booking?c=listBooks
                    * booking?c=book
                    * booking?c=deliver&subscription=true
                    * booking?c=cancel
                    * booking?c=done

                    /booking?command=listBooks&subscription=true
                    /booking?command=search&userID=?
                    /booking?c=search& user(email), booking(state) book(title, author, isbn, )
         */
        logger.debug("Init servlet...");
        commands = new HashMap<>();
        commands.put("addBook", BookingLogic::addBook);
        commands.put("removeBook", BookingLogic::removeBook);
        commands.put("listBooks", BookingLogic::listBooks);
        commands.put("search", BookingLogic::search);
        commands.put("basket", BookingLogic::basket);
        commands.put("book", BookingLogic::book);
        commands.put("deliver", BookingLogic::deliver);
        commands.put("cancel", BookingLogic::cancel);
        commands.put("done", BookingLogic::done);
        logger.debug("Done");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        proceed(req, resp);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        proceed(req, resp);
    }

    private void proceed(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.debug("Start proceeding...");
        String command = req.getParameter("command");
        logger.trace("command = {}", command);
        HttpSession session = req.getSession();

        session.removeAttribute(ServletAttributes.SERVICE_ERROR);
        User user = (User) session.getAttribute("user");

        if (command == null || !commands.containsKey(command)) {
            redirectToError(resp, "Illegal command request", session);
            return;
        }

        if (user == null) {
            redirectToError(resp, "Allowed only for logged users", session);
            return;
        }

        try {
            commands.get(command).execute(session, req);
        } catch (DaoException | ServiceException e) {
            redirectToError(resp, e.getMessage(), session);
        }

        String page = (String) req.getAttribute(ServletAttributes.PAGE);

        if (page == null) {
            page = req.getHeader("referer");
        }
        logger.debug("redirect to page={}", page);
        resp.sendRedirect(page);
    }

    private void redirectToError(HttpServletResponse resp, String msg, HttpSession session) throws IOException {
        logger.error(msg);
        session.setAttribute(ServletAttributes.SERVICE_ERROR, msg);
        resp.sendRedirect(Pages.ERROR);
    }
}
