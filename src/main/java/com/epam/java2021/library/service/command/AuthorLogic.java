package com.epam.java2021.library.service.command;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.dao.AuthorDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.factory.DaoFactoryImpl;
import com.epam.java2021.library.entity.impl.Author;
import com.epam.java2021.library.entity.impl.I18AuthorName;
import com.epam.java2021.library.entity.impl.Lang;
import com.epam.java2021.library.exception.AjaxException;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.validator.SafeRequest;
import com.epam.java2021.library.service.validator.SafeSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.epam.java2021.library.constant.Common.END_MSG;
import static com.epam.java2021.library.constant.Common.START_MSG;
import static com.epam.java2021.library.constant.ServletAttributes.*;

public class AuthorLogic {
    private static final Logger logger = LogManager.getLogger(AuthorLogic.class);
    private static final DaoFactoryImpl daoFactory = DaoFactoryCreator.getDefaultFactory().newInstance();

    private AuthorLogic() {}

    private static Author getValidParams(HttpServletRequest req) throws ServiceException {
        logger.debug(START_MSG);

        SafeRequest safeReq = new SafeRequest(req);
        String primaryLang = safeReq.get(JSP_AUTHOR_FORM_PRIMARY_LANG).notEmpty().convert();
        logger.trace("primaryLang={}", primaryLang);
        List<Lang> supported = (List<Lang>) req.getServletContext().getAttribute(SUPPORTED_LANGUAGES);
        if (supported == null) {
            throw new ServiceException("error.supported.langs.not.found");
        }

        Author.Builder builder = new Author.Builder();
        List<I18AuthorName> i18Names = new ArrayList<>();
        String primaryName = null;
        for (Lang lang: supported) {
            I18AuthorName.Builder i18Builder = new I18AuthorName.Builder();
            String i18name = safeReq.get(lang.getCode()).convert();
            if (!i18name.equals("")) {
                logger.trace("i18name={}, lang={}", i18name, lang);
                i18Builder.setLang(lang);
                i18Builder.setName(i18name);
                i18Names.add(i18Builder.build());
                if (lang.getCode().equals(primaryLang)) {
                    primaryName = i18name;
                    logger.trace("found primaryName: {}", i18name);
                }
            }
        }

        if (primaryName == null) {
            throw new ServiceException("error.primary.name.is.empty");
        }
        builder.setName(primaryName);
        builder.setI18Names(i18Names);
        builder.setModified(Calendar.getInstance());

        Author author = builder.build();
        logger.trace("got author params={}", author);
        logger.debug(END_MSG);

        return author;
    }

    public static String add(HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);
        String errorPage = Pages.AUTHOR_EDIT + "?command=author.add";

        Author author;
        try {
            author = getValidParams(req);
        } catch (ServiceException e) {
            return errorPageLogic(req, e.getMessage(), errorPage);
        }

        AuthorDao dao = daoFactory.getAuthorDao();
        Author existed = dao.read(author.getName());

        if (existed == null) {
            dao.create(author);
        } else {
            return errorPageLogic(req, "error.author.exists", errorPage);
        }

        logger.debug(END_MSG);
        return nextPageLogic(req);
    }

    private static String errorPageLogic(HttpServletRequest req, String msg, String page) {
        logger.debug(msg);
        req.getSession().setAttribute(USER_ERROR, msg);
        return page;
    }

    public static String edit(HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);
        String userErrorPage = Pages.AUTHOR_EDIT + "?command=author.edit";

        HttpSession session = req.getSession();
        SafeRequest safeRequest = new SafeRequest(req);
        try {
            long id = safeRequest.get(JSP_FORM_ATTR_ID).notNull().convert(Long::parseLong);
            AuthorDao dao = daoFactory.getAuthorDao();
            Author author = dao.read(id);
            session.setAttribute(ATTR_PROCEED_AUTHOR, author);
            logger.debug("set {} to {}", ATTR_PROCEED_AUTHOR, author);
            return Pages.AUTHOR_EDIT;
        } catch (ServiceException e) {
            logger.debug("id is not in the request, proceed with author editing");
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }

        Author params = null;
        try {
            params = getValidParams(req);
        } catch (ServiceException e) {
            errorPageLogic(req, e.getMessage(), userErrorPage);
        }

        SafeSession safeSession = new SafeSession(session);
        Author proceed = safeSession.get(ATTR_PROCEED_AUTHOR).notNull().convert(Author.class::cast);

        proceed.setName(params.getName());
        proceed.setI18Names(params.getI18Names());

        AuthorDao dao = daoFactory.getAuthorDao();
        dao.update(proceed);

        logger.debug(END_MSG);
        return nextPageLogic(req);
    }

    public static String delete(HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        SafeRequest safeRequest = new SafeRequest(req);

        long id = safeRequest.get(JSP_FORM_ATTR_ID).notNull().convert(Long::parseLong);
        AuthorDao dao = daoFactory.getAuthorDao();
        dao.delete(id);

        logger.debug(END_MSG);
        return nextPageLogic(req);
    }

    public static String find(HttpServletRequest req) throws ServiceException {
        logger.debug(START_MSG);

        return CommonLogic.find(req, daoFactory.getAuthorDao(), ATTR_AUTHORS, "author", Pages.AUTHORS);
    }

    public static String findAll(HttpServletRequest req) throws ServiceException, AjaxException {
        logger.debug(START_MSG);

        SafeRequest safeReq = new SafeRequest(req);
        String query = safeReq.get("query").escape().convert();
        String searchBy = safeReq.get("searchBy").notEmpty().escape().convert().toLowerCase();

        logger.trace("query={}, searchBy={}", query, searchBy);
        List<Author> list;
        try {
            list = daoFactory.getAuthorDao().findByPattern(query);
        } catch (DaoException e) {
            throw new AjaxException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        if (list.isEmpty()) {
            throw new AjaxException(HttpServletResponse.SC_NOT_FOUND, "error.not.found");
        }

        req.setAttribute(ATTR_AUTHORS, list);
        logger.debug(END_MSG);
        throw new AjaxException(Pages.XML_AUTHOR);
    }

    private static String nextPageLogic(HttpServletRequest req) throws ServiceException {
        SafeSession safeSession = new SafeSession(req.getSession());
        String page = safeSession.get(ATTR_SEARCH_LINK).convert(String.class::cast);
        if (page == null) {
            logger.trace("no previous search link in session");
            page = Pages.AUTHORS;
        }
        return page;
    }
}
