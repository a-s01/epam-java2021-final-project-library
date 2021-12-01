package com.epam.java2021.library.service;


import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.dao.UserDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.entity.impl.Lang;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.exception.UserException;
import com.epam.java2021.library.service.util.PasswordUtil;
import com.epam.java2021.library.service.util.SafeContext;
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

import static com.epam.java2021.library.constant.Common.END_MSG;
import static com.epam.java2021.library.constant.Common.START_MSG;
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

    public static String add(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);
        String errorPage = Pages.REGISTER + "?command=user.add";

        User user;
        try {
            user = getValidParams(session, req);
            if (user.getPassword().equals("")) {
                throw new UserException("error.password.is.empty");
            }
        } catch (UserException e) {
            session.setAttribute(USER_ERROR, e.getMessage());
            return errorPage;
        }
        
        try {
            String salt = PasswordUtil.genSalt();
            String pass = PasswordUtil.genHash(user.getPassword(), salt);
            user.setSalt(salt);
            user.setPassword(pass);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new ServiceException("error.password.generation");
        }

        user.setModified(Calendar.getInstance());
        user.setFineLastChecked(Calendar.getInstance());

        UserDao dao = daoFactory.getUserDao();
        try {
            dao.create(user);
        } catch (DaoException e) {
            try {
                if (e.getMessage().startsWith("Duplicate entry") && e.getMessage().endsWith("for key 'user.email'")) {
                    throw new UserException("error.duplicated.user.email");
                } else {
                    throw e;
                }
            } catch (UserException userE) {
                session.setAttribute(USER_ERROR, userE.getMessage());
                return errorPage;
            }
        }

        SafeSession safeSession = new SafeSession(session);
        User currentUser = safeSession.get(USER).convert(User.class::cast);

        String page;
        if (currentUser != null) {
            page = Pages.USERS;
        } else {
            session.setAttribute(USER, user);
            page = Pages.HOME;
        }

        logger.debug(END_MSG);
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
            session.setAttribute(SERVICE_ERROR, "error.app.general");
            logger.trace("Forward to error page '{}'", page);
        }

        if (user == null) {
            logger.trace("User not found");
            session.setAttribute(USER_ERROR, "error.incorrect.login");
            page = Pages.LOGIN;
        } else {
            logger.trace("User '{}' authenticated successfully", user.getEmail());
            session.setAttribute(USER, user);
            session.setAttribute(LANG, user.getPreferredLang());
            logger.trace("Added attributes to session: {}={}", USER, user);
            page = Pages.HOME;
        }

        logger.debug(END_MSG);
        return page;
    }

    private static User getValidParams(HttpSession session, HttpServletRequest req) throws UserException, ServiceException {
        logger.debug(START_MSG);
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
        Lang preferredLang;
        User currentUser = safeSession.get(USER).convert(User.class::cast);

        if (currentUser != null) {
            editBy = currentUser.getId();

            preferredLang = (Lang) req.getServletContext().getAttribute(DEFAULT_LANG);
            if (preferredLang == null) {
                throw new ServiceException("error.default.app.language.not.found");
            }
        } else {
            preferredLang = safeSession.get(LANG).notNull().convert(Lang.class::cast);
        }

        logger.trace("email={}, role={}, name={}, editBy={}, comment={}, preferedLang={}",
                email, role, name, editBy, comment, preferredLang);

        User.Builder uBuilder = new User.Builder();
        uBuilder.setEmail(email);
        if (!role.equals("")) {
            try {
                uBuilder.setRole(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ServiceException("error.wrong.user.role");
            }
        }
        uBuilder.setName(name);
        uBuilder.setPassword(pass);
        uBuilder.setPreferredLang(preferredLang);
        // TODO add history
        
        logger.debug(END_MSG);
        return uBuilder.build();
    }


    public static String edit(HttpSession session, HttpServletRequest req) throws DaoException, ServiceException {
        logger.debug(START_MSG);

        SafeRequest safeRequest = new SafeRequest(req);
        try {
            long userID = safeRequest.get("id").notEmpty().convert(Long::parseLong);
            logger.trace("id={}", userID);
            UserDao dao = daoFactory.getUserDao();
            User user = dao.read(userID);
            session.setAttribute(ATTR_PROCEED_USER, user);
            return Pages.REGISTER;
        } catch (ServiceException e) {
            logger.trace("id isn't in request, try to proceed with user editing");
        }

        SafeSession safeSession = new SafeSession(session);
        User proceedUser = safeSession.get(ATTR_PROCEED_USER).notNull().convert(User.class::cast);

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
                throw new ServiceException("error.password.generation");
            }
        }
        proceedUser.setRole(params.getRole());
        proceedUser.setName(params.getName());
        proceedUser.setModified(Calendar.getInstance());
        // TODO add history

        UserDao dao = daoFactory.getUserDao();
        dao.update(proceedUser);

        logger.debug(END_MSG);
        return nextPageLogic(safeSession);
    }

    private static String nextPageLogic(SafeSession safeSession) throws ServiceException {
        String page = safeSession.get(ATTR_SEARCH_LINK).convert(String.class::cast);
        if (page == null) {
            logger.trace("no previous search link in session");
            page = Pages.USERS;
        }
        return page;
    }

    public static String logout(HttpSession session, HttpServletRequest req) throws ServiceException {
        logger.debug(START_MSG);
        logger.trace("Session: id={}", session.getId());

        //Booking booking // TODO before invalidate load user booking to db and save it in cookie

        SafeSession safeSession = new SafeSession(session);
        Lang lang = safeSession.get(LANG).convert(Lang.class::cast);

        session.invalidate();

        if (lang != null) {
            logger.trace("saving user language: lang={}", lang);
            session = req.getSession();
            session.setAttribute(LANG, lang);
        }

        logger.debug(END_MSG);
        return Pages.LOGIN;
    }

    public static String delete(HttpSession session, HttpServletRequest request) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        SafeRequest safeReq = new SafeRequest(request);
        long id = safeReq.get("id").notNull().convert(Long::parseLong);

        UserDao dao = daoFactory.getUserDao();
        dao.delete(id);

        SafeSession safeSession = new SafeSession(session);
        List<User> users = safeSession.get(ATTR_USERS).convert(List.class::cast);
        if (users != null) {
            for (User u: users) {
                if (u.getId() == id) {
                    users.remove(u);
                    break;
                }
            }
        }

        logger.debug(END_MSG);
        return nextPageLogic(safeSession);
    }


    public static String find(HttpSession session, HttpServletRequest req) throws ServiceException {
        logger.debug(START_MSG);
        return CommonLogic.find(session, req, daoFactory.getUserDao(), ATTR_USERS, "user", Pages.USERS);
    }

    public static String setLang(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);
        SafeRequest safeReq = new SafeRequest(req);

        String langRequest = safeReq.get(LANG).notEmpty().convert();
        logger.trace("found lang in request: {}", langRequest);

        SafeContext context = new SafeContext(req.getServletContext());
        List<Lang> supported = context.get(SUPPORTED_LANGUAGES).notNull().convert(List.class::cast);

        Lang lang = null;
        for (Lang l: supported) {
            if (l.getCode().equals(langRequest)) {
                lang = l;
            }
        }

        if (lang != null) {
            session.setAttribute(LANG, lang);
            logger.trace("language applied to session: {}", lang);

            User user = (User) session.getAttribute(USER);
            if (user != null) {
                user.setPreferredLang(lang);
                UserDao userDao = daoFactory.getUserDao();
                userDao.update(user);
                logger.trace("language saved for user as prefered: user={}, lang={}", user, lang);
            }
        } else {
            throw new ServiceException("error.requested.lang.is.not.supported");
        }

        String url = safeReq.get(URL).notEmpty().convert();
        logger.trace("proceed back to url={}", url);
        logger.debug(END_MSG);
        return url;
    }
}