package com.epam.java2021.library.service;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.constant.ServletAttributes;
import com.epam.java2021.library.dao.UserDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.entity.impl.Booking;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.exception.UserException;
import com.epam.java2021.library.service.util.PasswordUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.epam.java2021.library.constant.ServletAttributes.*;
import static com.epam.java2021.library.constant.ServletAttributes.USER;

public class UserLogic {
    private static final Logger logger = LogManager.getLogger(UserLogic.class);
    private static final IDaoFactoryImpl daoFactory = DaoFactoryCreator.getDefaultFactory().getDefaultImpl();
    private static final String ERROR_DESCR = "{}: {}";

    private UserLogic() {}

    public static User getUser(String email, String password) throws DaoException, ServiceException {
        logger.trace("getUser request: email={}", email);

        UserDao userDao = daoFactory.getUserDao();
        User user = userDao.findByEmail(email);

        if (user != null) {
            try {
                String hash = PasswordUtil.genHash(password, user.getSalt());
                if (hash.equals(user.getPassword())) {
                    return user;
                }
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                final String msg = "Password hashing issue";
                logger.error(ERROR_DESCR, msg, e.getMessage());
                throw new ServiceException(msg, e);
            }
        }
        return null;
    }

    public static User createUser(String email, String pass, String role, String name, long editBy, String comment)
            throws ServiceException, DaoException, UserException {
        logger.trace("createUser request: email={}", email);

        if (email == null || email.equals("")) {
            throw new UserException("Email cannot be null");
        }
        if (pass == null || pass.equals("")) {
            throw new UserException("Password cannot be null");
        }

        User.Builder uBuilder = new User.Builder();
        uBuilder.setEmail(email);
        try {
            String salt = PasswordUtil.genSalt();
            pass = PasswordUtil.genHash(pass, salt);
            uBuilder.setSalt(salt);
            uBuilder.setPassword(pass);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new ServiceException("Unable to get salt/pass for user " + email);
        }
        uBuilder.setRole(role);
        uBuilder.setName(name);
        User user = uBuilder.build();
        // TODO add history

        UserDao dao = daoFactory.getUserDao();
        dao.create(user);
        return user;
    }

    public static void login(HttpSession session, HttpServletRequest req) {
        logger.debug("start");
        String email = req.getParameter("email");
        String pass = req.getParameter("password");
        logger.trace("Auth request for email '{}'", email);
        User user = null;

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

        req.setAttribute(ServletAttributes.PAGE, page);
        logger.debug("end");
    }

    public static void register(HttpSession session, HttpServletRequest req) {
        String email = req.getParameter(ServletAttributes.REG_EMAIL);
        logger.trace("Request for creating new user: '{}'", email);
        String pass = req.getParameter(ServletAttributes.REG_PASS);
        String role = req.getParameter("role");
        String name = req.getParameter("name");
        String comment = req.getParameter("comment");
        long editBy = -1;

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

        req.setAttribute(ServletAttributes.PAGE, page);
    }

    public static void logout(HttpSession session, HttpServletRequest req) {
        logger.debug("start");
        logger.trace("Session: id={}", session.getId());

        //Booking booking // TODO before invalidate load user booking to db and save it in cookie

        session.invalidate();

        req.setAttribute(ServletAttributes.PAGE, Pages.HOME);
        logger.debug("end");
    }

    public static void edit(HttpSession session, HttpServletRequest request) {
    }

    public static void delete(HttpSession session, HttpServletRequest request) {
    }
}