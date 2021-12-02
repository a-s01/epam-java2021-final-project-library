package com.epam.java2021.library.service.command;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.constant.ServletAttributes;
import com.epam.java2021.library.dao.AuthorDao;
import com.epam.java2021.library.dao.BookDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.factory.DaoFactoryImpl;
import com.epam.java2021.library.entity.impl.Author;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.entity.impl.BookStat;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.validator.SafeRequest;
import com.epam.java2021.library.service.validator.SafeSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.epam.java2021.library.constant.Common.END_MSG;
import static com.epam.java2021.library.constant.Common.START_MSG;
import static com.epam.java2021.library.constant.ServletAttributes.*;

public class BookLogic {
    private static final Logger logger = LogManager.getLogger(BookLogic.class);
    private static final DaoFactoryImpl daoFactory = DaoFactoryCreator.getDefaultFactory().newInstance();

    private BookLogic() {}

    public static String find(HttpServletRequest req) throws ServiceException {
        logger.debug("start");
        BookDao dao = daoFactory.getBookDao();
        return CommonLogic.find(req, dao, ATTR_BOOKS, "book", Pages.HOME);
    }

    private static Book getValidParams(HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);
        SafeRequest safeReq = new SafeRequest(req);

        String title = safeReq.get("title").notEmpty().escape().convert();
        String isbn = safeReq.get("isbn").notEmpty().escape().convert();
        int year = safeReq.get("year").notEmpty().convert(Integer::parseInt);
        int keepPeriod = safeReq.get("keepPeriod").notEmpty().convert(Integer::parseInt); // TODO add default value to JSP
        long total = safeReq.get("total").notEmpty().convert(Long::parseLong);
        String langCode = safeReq.get("langCode").notEmpty().convert();
        String[] authorIDsAsStr = req.getParameterValues(ATTR_AUTHOR_IDS);

        if (authorIDsAsStr == null) {
            throw new ServiceException("error.author.ids.null");
        }

        List<Author> authors = new ArrayList<>(authorIDsAsStr.length);
        AuthorDao authorDao = daoFactory.getAuthorDao();
        for (String s : authorIDsAsStr) {
            try {
                long id = Long.parseLong(s);
                authors.add(authorDao.read(id));
            } catch (NumberFormatException e) {
                throw new ServiceException("error.wrong.number.format");
            }
        }

        if (!isValidISOLanguage(langCode)) {
            throw new ServiceException("error.wrong.language.code");
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (year < 1500 || year > currentYear) {
            throw new ServiceException("error.invalid.year");
        }

        logger.trace("title={}, year={}, isbn={}, total={}, keepPeriod={}", title, year, isbn, total, keepPeriod);
        logger.trace("authors={}", authors);
        Book.Builder builder = new Book.Builder();
        builder.setTitle(title);
        builder.setYear(year);
        builder.setIsbn(isbn);
        builder.setKeepPeriod(keepPeriod);
        builder.setBookStat(new BookStat.Builder().setTotal(total).build());
        builder.setLangCode(langCode);
        builder.setAuthors(authors);

        logger.debug(END_MSG);
        return builder.build();
    }

    private static boolean isValidISOLanguage(String langCode) {
        logger.debug("check if {} is a valid ISO-639 code", langCode);
        for (String code: Locale.getISOLanguages()) {
            if (code.equals(langCode)) {
                logger.debug("{} is valid", langCode);
                return true;
            }
        }
        logger.debug("{} is not valid", langCode);
        return false;
    }

    public static String add(HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        Book book;
        try {
            book = getValidParams(req);
        } catch (ServiceException e) {
            req.getSession().setAttribute(ServletAttributes.USER_ERROR, e.getMessage());
            return Pages.BOOK_EDIT + "?command=book.add";
        }
        book.setModified(Calendar.getInstance());
        BookDao dao = daoFactory.getBookDao();
        dao.create(book);

        logger.debug(END_MSG);
        return Pages.HOME;
    }

    public static String edit(HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);
        String errorPage = Pages.BOOK_EDIT + "?command=book.edit";

        HttpSession session = req.getSession();
        SafeRequest safeReq = new SafeRequest(req);
        try {
            long bookID = safeReq.get("id").notEmpty().convert(Long::parseLong);
            logger.trace("id={}", bookID);

            BookDao dao = daoFactory.getBookDao();
            Book book = dao.read(bookID);
            session.setAttribute(ATTR_PROCEED_BOOK, book);

            logger.debug(END_MSG);
            return Pages.BOOK_EDIT;
        } catch (ServiceException e) {
            // it doesn't have id, move on
            logger.trace("no id was passed, try to proceed with book editing");
        }

        Book params;
        try {
            params = getValidParams(req);
        } catch (ServiceException e) {
            session.setAttribute(ServletAttributes.USER_ERROR, e.getMessage());
            return errorPage;
        }
        
        SafeSession safeSession = new SafeSession(session);
        Book proceed = safeSession.get(ATTR_PROCEED_BOOK).notNull().convert(Book.class::cast);
        session.removeAttribute(ATTR_PROCEED_BOOK);

        proceed.setIsbn(params.getIsbn());
        proceed.setYear(params.getYear());
        proceed.setTitle(params.getTitle());
        proceed.setKeepPeriod(params.getKeepPeriod());
        proceed.setModified(Calendar.getInstance());
        proceed.setLangCode(params.getLangCode());
        proceed.setAuthors(params.getAuthors());

        BookStat old = proceed.getBookStat();
        long totalInStockDiff = old.getTotal() - old.getInStock();
        old.setTotal(params.getBookStat().getTotal());
        long newInStock = params.getBookStat().getTotal() - totalInStockDiff;
        if (newInStock < 0) {
            session.setAttribute(ServletAttributes.USER_ERROR, "In stock reminder should be more than 0");
            return errorPage;
        }
        old.setInStock(newInStock);
        
        BookDao dao = daoFactory.getBookDao();
        dao.update(proceed);

        logger.debug(END_MSG);
        return nextPageLogic(safeSession);
    }

    private static String nextPageLogic(SafeSession safeSession) throws ServiceException {
        String page = safeSession.get(ATTR_SEARCH_LINK).convert(String.class::cast);
        if (page == null) {
            logger.trace("previous search is null, set page to home={}", Pages.HOME);
            page = Pages.HOME;
        }
        return page;
    }

    public static String delete(HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        SafeRequest safeReq = new SafeRequest(req);
        long id = safeReq.get("id").notNull().convert(Long::parseLong);

        SafeSession safeSession = new SafeSession(req.getSession());
        List<Book> books = safeSession.get(ATTR_BOOKS).convert(List.class::cast);
        if (books != null) {
            for (Book b: books) {
                if (b.getId() == id) {
                    books.remove(b);
                    req.getSession().setAttribute(ATTR_BOOKS, books);
                    logger.debug("books in session updated");
                    break;
                }
            }
        }

        BookDao dao = daoFactory.getBookDao();
        dao.delete(id);

        logger.debug(END_MSG);
        return nextPageLogic(safeSession);
    }
}
