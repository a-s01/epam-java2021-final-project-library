package com.epam.java2021.library.controller.filter;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.util.Captcha;
import com.github.cage.Cage;
import com.github.cage.GCage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.epam.java2021.library.constant.ServletAttributes.*;

public class CaptchaGenerator implements Filter {
    private static final Logger logger = LogManager.getLogger(CaptchaGenerator.class);
    private final Cage cage = new GCage();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) servletRequest).getSession();

        User currentUser = (User) session.getAttribute(USER);
        if (currentUser != null) { //means user is authenticated
            filterChain.doFilter(servletRequest, servletResponse);
        }

        try {
            Captcha captcha = new Captcha();
            session.setAttribute("captcha", captcha);
            logger.trace("captcha saved to session {}", captcha);
        } catch (ServiceException e) {
            session.setAttribute(SERVICE_ERROR, e);
            session.setAttribute(SERVICE_ERROR_PARAMETERS, e.getMsgParameters());
            servletRequest.getRequestDispatcher(Pages.ERROR).forward(servletRequest, servletResponse);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
