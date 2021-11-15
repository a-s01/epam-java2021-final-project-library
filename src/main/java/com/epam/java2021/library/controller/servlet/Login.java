package com.epam.java2021.library.controller.servlet;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.entity.entityImpl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.util.UserLogic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.epam.java2021.library.constant.ServletAttributes.ERROR_ATTR;

@WebServlet("/login")
public class Login extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(Login.class);
    private static final String THIS_PAGE = "/login";

    // TODO do something with exceptions
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String pass = req.getParameter("password");
        logger.trace("Auth request for email {}", email);
        User user = null;

        HttpSession session = req.getSession();
        try {
            user = UserLogic.getUser(email, pass);
        } catch (DaoException | ServiceException e) {
            session.setAttribute(ERROR_ATTR, "Error in app working. Please, try again later.");
        }
        session.setAttribute("user", user);
        resp.sendRedirect(THIS_PAGE);
    }

    // TODO do something with exceptions
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //ServletContext context = getServletContext();
        HttpSession session = req.getSession();
        Object errorMsg = session.getAttribute(ERROR_ATTR);

        if (errorMsg != null) {
            String page = Pages.ERROR;
            logger.trace("Forward to error page {}", page);
            req.setAttribute(ERROR_ATTR, errorMsg);
            session.removeAttribute(ERROR_ATTR);
            //context.getRequestDispatcher(page).forward(req, resp);
            req.getRequestDispatcher(page).forward(req, resp);
            return;
        }

        User user = (User) session.getAttribute("user");
        String page;

        if (user == null) {
            logger.trace("No such user was found");
            req.setAttribute(ERROR_ATTR, "Incorrect login or password");
            page = Pages.LOGIN;
        } else {
            logger.trace("User {} authenticated", user.getEmail());
            page = Pages.HOME;
        }
        //context.getRequestDispatcher(page).forward(req, resp);
        req.getRequestDispatcher(page).forward(req, resp);
    }
}
