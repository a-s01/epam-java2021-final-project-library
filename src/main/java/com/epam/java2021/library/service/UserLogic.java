package com.epam.java2021.library.service;


import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.dao.UserDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.exception.UserException;
import com.epam.java2021.library.service.util.PasswordUtil;
import com.epam.java2021.library.service.util.SafeRequest;
import com.epam.java2021.library.service.util.SafeSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.List;

import static com.epam.java2021.library.constant.ServletAttributes.*;

public class UserLogic {
    private static final Logger logger = LogManager.getLogger(UserLogic.class);
    private static final IDaoFactoryImpl daoFactory = DaoFactoryCreator.getDefaultFactory().getDefaultImpl();
    private static final String ERROR_DESCR = "{}: {}";

    
    private UserLogic() {}

    private static User getUser(String email, String password) throws DaoException, ServiceException {
        logger.debug(START_MSG);
        logger.trace("getUser request: email={}", email);

        UserDao userDao = daoFactory.getUserDao();
        User user = userDao.findByEmail(email);

        if (user != null) {
            try {
                String hash = PasswordUtil.genHash(password, user.getSalt());
                if (hash.equals(user.getPassword())) {
                    logger.trace("found: user={}", user);
                    logger.debug(END_MSG);
                    return user;
                }
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                final String msg = "Password hashing issue";
                logger.error(ERROR_DESCR, msg, e.getMessage());
                throw new ServiceException(msg, e);
            }
        }
        logger.trace("user not found");
        logger.debug(END_MSG);
        return null;
    }

    public static String register(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        User user;
        try {
            user = getValidParams(session, req);
            if (user.getPassword().equals("")) {
                throw new UserException("Password cannot be empty");
            }
        } catch (UserException e) {
            session.setAttribute(USER_ERROR, e.getMessage());
            return Pages.REGISTER;
        }
        
        try {
            String salt = PasswordUtil.genSalt();
            String pass = PasswordUtil.genHash(user.getPassword(), salt);
            user.setSalt(salt);
            user.setPassword(pass);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new ServiceException("Unable to get salt/pass for user " + user.getEmail());
        }

        user.setModified(Calendar.getInstance());

        UserDao dao = daoFactory.getUserDao();
        try {
            dao.create(user);
        } catch (DaoException e) {
            try {
                if (e.getMessage().startsWith("Duplicate entry") && e.getMessage().endsWith("for key 'user.email'")) {
                    throw new UserException("Email is already taken");
                } else {
                    throw e;
                }
            } catch (UserException userE) {
                session.setAttribute(USER_ERROR, userE.getMessage());
                return Pages.REGISTER;
            }
        }

        SafeSession safeSession = new SafeSession(session);
        User currentUser = safeSession.get(USER).convert(User.class::cast);

        String page;
        if (currentUser != null) {
            page = Pages.USERS;
        } else {
            session.setAttribute(USER, user);
            logger.debug(END_MSG);
            page = Pages.HOME;
        }
        return page;
    }

    public static String login(HttpSession session, HttpServletRequest req) {
        logger.debug(START_MSG);
        String email = req.getParameter("email");
        String pass = req.getParameter("password");
        logger.trace("Auth request for email '{}'", email);
        User user = null;

        logger.trace("Session id: {}", session.getId());

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
            session.setAttribute(USER_ERROR, "Incorrect login or password");
            page = Pages.LOGIN;
        } else {
            logger.trace("User '{}' authenticated successfully", user.getEmail());
            session.setAttribute(USER, user);
            logger.trace("Added attributes to session: {}={}", USER, user);
            page = Pages.HOME;
        }

        logger.debug(END_MSG);
        return page;
    }

    private static User getValidParams(HttpSession session, HttpServletRequest req) throws UserException, ServiceException {
        String email;
        String pass;
        String role;
        
        SafeRequest safeReq = new SafeRequest(req);

        try {
            email = safeReq.get(EMAIL).notEmpty().asEmail().convert();
        } catch (ServiceException e) {
            session.setAttribute(USER_ERROR, e.getMessage());
            throw new UserException(e.getMessage());
        }
        pass = safeReq.get(PASS).convert();
        role = safeReq.get("role").escape().convert();

        String name = safeReq.get("name").escape().convert();
        String comment = safeReq.get("comment").escape().convert();

        SafeSession safeSession = new SafeSession(session);
        long editBy = -1;
        User currentUser = safeSession.get(USER).convert(User.class::cast);
        if (currentUser != null) {
            editBy = currentUser.getId();
        }

        logger.trace("email={}, role={}, name={}, editBy={}, comment={}", 
                email, role, name, editBy, comment);

        User.Builder uBuilder = new User.Builder();
        uBuilder.setEmail(email);
        if (!role.equals("")) {
            try {
                uBuilder.setRole(role);
            } catch (IllegalArgumentException e) {
                throw new ServiceException("No such user role: " + role);
            }
        }
        uBuilder.setName(name);
        uBuilder.setPassword(pass);
        // TODO add history
        return uBuilder.build();
    }


    public static String edit(HttpSession session, HttpServletRequest req) throws DaoException, ServiceException {
        logger.debug(START_MSG);

        SafeRequest safeRequest = new SafeRequest(req);
        long userID = safeRequest.get("userID").notNull().convert(Long::parseLong);
        logger.trace("userID={}", userID);
        User proceedUser = null;

        SafeSession safeSession = new SafeSession(session);
        List<User> users = safeSession.get(USERS).notNull().convert(List.class::cast);
        for (User u : users) {
            if (u.getId() == userID) {
                proceedUser = u;
                break;
            }
        }

        if (proceedUser == null) {
            throw new ServiceException("User should be in cached list of users");
        }

        User params;
        try {
            params = getValidParams(session, req);
        } catch (UserException e) {
            return Pages.REGISTER;
        }

        proceedUser.setEmail(params.getEmail());
        if (!params.getPassword().equals("")) {
            String salt = proceedUser.getSalt();
            try {
                String pass = PasswordUtil.genHash(params.getPassword(), salt);
                proceedUser.setPassword(pass);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new ServiceException("Unable to get pass for user " + params.getEmail());
            }
        }
        proceedUser.setRole(params.getRole());
        proceedUser.setName(params.getName());
        proceedUser.setModified(Calendar.getInstance());
        // TODO add history

        UserDao dao = daoFactory.getUserDao();
        dao.update(proceedUser);

        logger.debug(END_MSG);
        return Pages.USERS;
    }

    public static String logout(HttpSession session, HttpServletRequest req) {
        logger.debug(START_MSG);
        logger.trace("Session: id={}", session.getId());

        //Booking booking // TODO before invalidate load user booking to db and save it in cookie

        session.invalidate();

        logger.debug(END_MSG);
        return Pages.LOGIN;
    }

    public static String delete(HttpSession session, HttpServletRequest request) throws ServiceException, DaoException {
        SafeRequest safeReq = new SafeRequest(request);
        long id = safeReq.get("id").notNull().convert(Long::parseLong);

        UserDao dao = daoFactory.getUserDao();
        dao.delete(id);

        SafeSession safeSession = new SafeSession(session);
        List<User> users = safeSession.get(USERS).convert(List.class::cast);
        if (users != null) {
            for (User u: users) {
                if (u.getId() == id) {
                    users.remove(u);
                    break;
                }
            }
        }
        return Pages.USERS;
    }


    public static String find(HttpSession session, HttpServletRequest req) throws ServiceException {
        logger.debug(START_MSG);
        return CommonLogic.find(session, req, logger, daoFactory.getUserDao(), USERS, Pages.USERS);
    }
}