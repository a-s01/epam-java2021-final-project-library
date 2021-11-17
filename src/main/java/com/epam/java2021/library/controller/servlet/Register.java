package com.epam.java2021.library.controller.servlet;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.constant.ServletAttributes;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.exception.UserException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.epam.java2021.library.service.UserLogic.createUser;

@WebServlet("/register")
public class Register extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(Register.class);

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter(ServletAttributes.REG_EMAIL);
        logger.trace("Request for creating new user: '{}'", email);
        String pass = req.getParameter(ServletAttributes.REG_PASS);
        String role = req.getParameter("role");
        String name = req.getParameter("name");
        String comment = req.getParameter("comment");
        long editBy = -1;

        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");
        String page = null;

        if (currentUser != null) {
            editBy = currentUser.getId();
            page = Pages.USERS;
        }

        session.removeAttribute(ServletAttributes.REG_PAGE_ERROR_MSG);
        session.removeAttribute(ServletAttributes.ERROR_PAGE_ERROR_MSG);
        try {
            User created = createUser(email, pass, role, name, editBy, comment);
            logger.trace("User '{}' successfully created", email);
            if (currentUser == null) {
                session.setAttribute(ServletAttributes.USER, created);
                page = Pages.HOME;
            }
        } catch (ServiceException | DaoException e) {
            session.setAttribute(ServletAttributes.ERROR_PAGE_ERROR_MSG, e.getMessage());
            logger.trace("Service error in user '{}' creation", email);
            page = Pages.ERROR;
        } catch (UserException e) {
            session.setAttribute(ServletAttributes.REG_PAGE_ERROR_MSG, e.getMessage());
            logger.trace("User error in user '{}' creation", email);
            page = Pages.LOGIN;
        }

        resp.sendRedirect(page);
    }
}
