package com.epam.java2021.library.controller.servlet;

import com.epam.java2021.library.constant.Pages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class Logout extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(Logout.class);

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        logger.trace("Session invalidated: {}", session.getId());
        session.invalidate();
        req.getRequestDispatcher(Pages.HOME).forward(req, resp);
    }
}
