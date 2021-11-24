package com.epam.java2021.library.service;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.constant.ServletAttributes;
import com.epam.java2021.library.dao.BookDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.entity.impl.BookStat;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.util.SafeRequest;
import com.epam.java2021.library.service.util.SafeSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.List;

import static com.epam.java2021.library.constant.ServletAttributes.*;

public class BookLogic {
    private static final Logger logger = LogManager.getLogger(BookLogic.class);
    private static final IDaoFactoryImpl daoFactory = DaoFactoryCreator.getDefaultFactory().getDefaultImpl();

    private BookLogic() {}

    public static String find(HttpSession session, HttpServletRequest req) throws ServiceException {
        logger.debug("start");
        BookDao dao = daoFactory.getBookDao();
        return CommonLogic.find(session, req, logger, dao, BOOKS, Pages.HOME);
    }

    private static Book getValidParams(HttpServletRequest req) throws ServiceException {
        SafeRequest safeReq = new SafeRequest(req);

        String title = safeReq.get("title").notEmpty().escape().convert();
        String isbn = safeReq.get("isbn").notEmpty().escape().convert();
        int year = safeReq.get("year").notEmpty().convert(Integer::parseInt);
        int keepPeriod = safeReq.get("keepPeriod").convert(Integer::parseInt); // TODO add default value to JSP
        long total = safeReq.get("total").convert(Long::parseLong);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (year < 1500 || year > currentYear) {
            throw new ServiceException("Your year is in future, not accepted");
        }

        logger.trace("title={}, year={}, isbn={}, total={}, keepPeriod={}", title, year, isbn, total, keepPeriod);
        Book.Builder builder = new Book.Builder();
        builder.setTitle(title);
        builder.setYear(year);
        builder.setIsbn(isbn);
        builder.setKeepPeriod(keepPeriod);
        builder.setBookStat(new BookStat.Builder().setTotal(total).build());

        return builder.build();
    }
    public static String add(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        Book book;
        try {
            book = getValidParams(req);
        } catch (ServiceException e) {
            session.setAttribute(ServletAttributes.USER_ERROR, e.getMessage());
            return Pages.BOOK_EDIT;
        }
        book.setModified(Calendar.getInstance());
        // TODO authors
        BookDao dao = daoFactory.getBookDao();
        dao.create(book);

        logger.debug(END_MSG);
        return Pages.HOME;
    }

    public static String edit(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        Book params;
        try {
            params = getValidParams(req);
        } catch (ServiceException e) {
            session.setAttribute(ServletAttributes.USER_ERROR, e.getMessage());
            return Pages.BOOK_EDIT;
        }

        SafeRequest safeReq = new SafeRequest(req);
        long bookID = safeReq.get("bookID").convert(Long::parseLong);
        logger.trace("bookID={}", bookID);

        SafeSession safeSession = new SafeSession(session);
        List<Book> books = safeSession.get(BOOKS).notNull().convert(List.class::cast);

        Book proceed = null;
        for (Book b: books) {
            if (b.getId() == bookID) {
                proceed = b;
            }
        }

        // TODO they wont' be in session
        if (proceed == null) {
            throw new ServiceException("Edited book should be present in session");
        }

        proceed.setIsbn(params.getIsbn());
        proceed.setYear(params.getYear());
        proceed.setTitle(params.getTitle());
        proceed.setKeepPeriod(params.getKeepPeriod());
        proceed.setModified(Calendar.getInstance());

        BookStat old = proceed.getBookStat();
        long totalInStockDiff = old.getTotal() - old.getInStock();
        old.setTotal(params.getBookStat().getTotal());
        long newInStock = params.getBookStat().getTotal() - totalInStockDiff;
        if (newInStock < 0) {
            session.setAttribute(ServletAttributes.USER_ERROR, "In stock reminder should be more than 0");
            return Pages.BOOK_EDIT;
        }
        old.setInStock(newInStock);
        BookDao dao = daoFactory.getBookDao();
        dao.update(proceed);

        logger.debug(END_MSG);
        return Pages.HOME;
    }

    public static String delete(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        SafeRequest safeReq = new SafeRequest(req);
        long id = safeReq.get("id").notNull().convert(Long::parseLong);

        SafeSession safeSession = new SafeSession(session);
        List<Book> books = safeSession.get(BOOKS).convert(List.class::cast);
        if (books != null) {
            for (Book b: books) {
                if (b.getId() == id) {
                    books.remove(b);
                    session.setAttribute(BOOKS, books);
                    logger.debug("books in session updated");
                    break;
                }
            }
        }

        BookDao dao = daoFactory.getBookDao();
        dao.delete(id);

        logger.debug(END_MSG);
        return Pages.HOME;
    }
}
