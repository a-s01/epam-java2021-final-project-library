package com.epam.java2021.library.service.util;

import com.epam.java2021.library.dao.EntityTransaction;
import com.epam.java2021.library.dao.UserDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.entity.entityImpl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class UserLogic {
    private static final Logger logger = LogManager.getLogger(UserLogic.class);
    private static final IDaoFactoryImpl daoFactory = DaoFactoryCreator.getDefaultFactory().getDefaultImpl();

    private static void logAndThrow(String msg, Exception e) throws DaoException {
        logger.error("{}: {}", msg, e.getMessage());
        throw new DaoException(msg, e);
    }

    public static User getUser(String login, String password) throws DaoException, ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        UserDao userDao = daoFactory.getUserDao();
        transaction.init(userDao);
        User user = userDao.findByEmail(login);
        transaction.end();

        if (user != null) {
            try {
                String hash = PasswordUtil.genHash(password, user.getSalt());
                if (hash == user.getPassword()) {
                    return user;
                }
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                final String msg = "Password hashing issue";
                logger.error("{}: {}", msg, e.getMessage());
                throw new ServiceException(msg, e);
            }
        }
        return null;
    }

    public static void createUser(String email, String pass, String role, String name, long editBy, String comment) throws ServiceException {
        if (email == null || email == "") {
            throw new ServiceException("Email cannot be null");
        }
        if (pass == null || pass == "") {
            throw new ServiceException("Password cannot be null");
        }

        User.Builder builder = new User.Builder();

    }
}