package com.epam.java2021.library.controller.servlet;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.constant.ServletAttributes;
import com.epam.java2021.library.entity.entityImpl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.UserLogic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.epam.java2021.library.constant.ServletAttributes.*;

@WebServlet("/login")
public class Login extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(Login.class);

    // TODO do something with exceptions
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String pass = req.getParameter("password");
        logger.trace("Auth request for email '{}'", email);
        User user = null;

        HttpSession session = req.getSession();
        logger.trace("Session id: {}", session.getId());
        session.removeAttribute(SERVICE_ERROR);
        session.removeAttribute(LOGIN_PAGE_ERROR_MSG);
        String page;
        try {
            user = UserLogic.getUser(email, pass);
        } catch (DaoException | ServiceException e) {
            page = Pages.ERROR;
            session.setAttribute(SERVICE_ERROR, "Error in app working. Please, try again later.");
            logger.trace("Forward to error page '{}'", page);
        }

        if (user == null) {
            logger.trace("User not found");
            session.setAttribute(LOGIN_PAGE_ERROR_MSG, "Incorrect login or password");
            page = Pages.LOGIN;
        } else {
            logger.trace("User '{}' authenticated successfully", user.getEmail());
            session.setAttribute(USER, user);
            logger.trace("Added attributes to session: {}={}", USER, user);
            page = Pages.HOME;
        }
        resp.sendRedirect(page);
    }
}
