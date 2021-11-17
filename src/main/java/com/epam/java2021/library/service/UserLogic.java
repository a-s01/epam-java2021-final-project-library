package com.epam.java2021.library.service;

import com.epam.java2021.library.dao.UserDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.exception.UserException;
import com.epam.java2021.library.service.util.PasswordUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
}