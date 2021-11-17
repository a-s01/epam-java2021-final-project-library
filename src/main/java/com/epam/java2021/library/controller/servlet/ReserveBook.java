package com.epam.java2021.library.controller.servlet;

import com.epam.java2021.library.entity.impl.Booking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/reserveBook")
public class ReserveBook extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(ReserveBook.class);

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String bookId = req.getParameter("id");
        HttpSession session = req.getSession();
        Booking booking = (Booking) session.getAttribute("booking");
        if (booking != null) {

        }
    }
}
