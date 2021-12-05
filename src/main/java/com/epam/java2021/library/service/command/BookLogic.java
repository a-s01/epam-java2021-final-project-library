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

/**
 * Class-util, has only static methods. All methods are related to Book, such as find book, edit book, etc.
 * All public methods here must comply with {@link com.epam.java2021.library.service.command.Command} signature, as
 * they will be used in CommandContext as lambda-functions and called from Front Controller
 * {@link com.epam.java2021.library.controller.servlet.Controller}
 */
public class BookLogic {
    private static final Logger logger = LogManager.getLogger(BookLogic.class);
    private static final DaoFactoryImpl daoFactory = DaoFactoryCreator.getDefaultFactory().newInstance();
    private static final String ATTR_BOOK_SEARCH_LINK = "book" + ATTR_SEARCH_LINK;
    private static final int BOOK_PRINTING_INVENTED = 1455;

    /**
     * Made private intentionally, no instance is needed by design
     */
    private BookLogic() {
    }

    /**
     * Finds all book by pattern provided in request, with pagination.
     *
     * @return next page to be seen by user
     * @throws ServiceException in case something is wrong with the request
     */
    public static String find(HttpServletRequest req) throws ServiceException {
        logger.debug(START_MSG);
        BookDao dao = daoFactory.getBookDao();
        return CommonLogicFunctions.findWithPagination(req, dao, ATTR_BOOKS, "book", Pages.HOME);
    }

    private static Book validateAndGetParams(HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        SafeRequest safeReq = new SafeRequest(req);

        String title = safeReq.get("title").notEmpty().escape().convert();
        String isbn = safeReq.get("isbn").notEmpty().escape().convert();
        int year = safeReq.get("year").notEmpty().convert(Integer::parseInt);
        int keepPeriod = safeReq.get("keepPeriod").notEmpty().convert(Integer::parseInt);
        // to JSP
        long total = safeReq.get("total").notEmpty().convert(Long::parseLong);
        String langCode = safeReq.get("langCode").notEmpty().convert();
        String[] authorIDsAsStr = req.getParameterValues(ATTR_AUTHOR_IDS);

        Book savedUserInput = new Book.Builder()
                .setTitle(title)
                .setYear(year)
                .setIsbn(isbn)
                .setKeepPeriod(keepPeriod)
                .setBookStat(new BookStat.Builder()
                        .setTotal(total)
                        .build())
                .setLangCode(langCode)
                .setModified(Calendar.getInstance())
                .build();

        // for user convenience we preserve his edition before throwing the error
        req.getSession().setAttribute(ATTR_SAVED_USER_INPUT, savedUserInput);

        if (authorIDsAsStr == null) {
            throw new ServiceException("error.author.ids.null");
        }

        // for the same reason the order of checks is such: authors first, others - later
        List<Author> authors = new ArrayList<>(authorIDsAsStr.length);
        savedUserInput.setAuthors(authors);

        AuthorDao authorDao = daoFactory.getAuthorDao();
        for (String s : authorIDsAsStr) {
            try {
                long id = Long.parseLong(s);
                Author author = authorDao.read(id);
                if (author == null) {
                    logger.error("author with id {} was not found", id);
                    throw new ServiceException("error.no.object.with.such.id");
                }
                authors.add(author);
            } catch (NumberFormatException e) {
                throw new ServiceException("error.wrong.number.format");
            }
        }

        if (!isValidISOLanguage(langCode)) {
            throw new ServiceException("error.wrong.language.code");
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (year < BOOK_PRINTING_INVENTED || year > currentYear) {
            throw new ServiceException("error.invalid.year");
        }

        logger.trace("title={}, year={}, isbn={}, total={}, keepPeriod={}", title, year, isbn, total, keepPeriod);
        logger.trace("authors={}", authors);
        logger.debug(END_MSG);
        return savedUserInput;
    }

    private static boolean isValidISOLanguage(String langCode) {
        logger.debug("check if {} is a valid ISO-639 code", langCode);
        for (String code : Locale.getISOLanguages()) {
            if (code.equals(langCode)) {
                logger.debug("{} is valid", langCode);
                return true;
            }
        }
        logger.debug("{} is not valid", langCode);
        return false;
    }

    /**
     * Adds a new book. In case of user/service error shows the same "add book" page with embedded error message.
     * Preserves user editing, keeping it in session and deletes it when need it no more
     *
     * @return next page to be seen by user
     * @throws ServiceException in case of programmer or dao error (mostly also programmer fault)
     */
    public static String add(HttpServletRequest req) throws ServiceException {
        logger.debug(START_MSG);

        try {
            Book book = validateAndGetParams(req);

            BookDao dao = daoFactory.getBookDao();
            List<Book> existInDB = dao.findBy(book.getIsbn(), "isbn");
            if (!existInDB.isEmpty()) {
                throw new ServiceException("error.duplicate.book.isbn");
            }

            dao.create(book);
        } catch (ServiceException | DaoException e) {
            req.getSession().setAttribute(ServletAttributes.USER_ERROR, e.getMessage());
            return Pages.BOOK_EDIT + "?command=book.add";
        } finally {
            logger.debug(END_MSG);
        }

        req.getSession().removeAttribute(ATTR_SAVED_USER_INPUT);
        return nextPageLogic(req);
    }

    /**
     * Edit the book. In case of user error shows the same "edit book" page with embedded error message.
     * Preserves user editing, keeping it in session and deletes it when need it no more
     *
     * @return next page to be seen by user
     * @throws ServiceException only in case of programmer error
     * @throws DaoException in case of dao error
     */
    public static String edit(HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        String page;
        try {
            page = editFindRequestedBookByID(req);
        } catch (ServiceException e) {
            // it doesn't have id, move on
            logger.trace("no id was passed, try to proceed with book editing");
            page = editBookPresentInSession(req);
        }

        logger.debug(END_MSG);
        return page;
    }

    private static String editFindRequestedBookByID(HttpServletRequest req) throws ServiceException, DaoException {
        SafeRequest safeReq = new SafeRequest(req);
        long bookID = safeReq.get("id").notEmpty().convert(Long::parseLong);
        logger.trace("id={}", bookID);

        BookDao dao = daoFactory.getBookDao();
        Book book = dao.read(bookID);
        req.getSession().setAttribute(ATTR_PROCEED_BOOK, book);
        logger.trace("book={}", book);
        return Pages.BOOK_EDIT;
    }

    private static String editBookPresentInSession(HttpServletRequest req) throws ServiceException {
        logger.debug(START_MSG);
        final String errorPage = Pages.BOOK_EDIT + "?command=book.edit";

        HttpSession session = req.getSession();
        SafeSession safeSession = new SafeSession(req.getSession());

        Book oldBookVersion = safeSession.get(ATTR_PROCEED_BOOK).notNull().convert(Book.class::cast);

        Book updatedBookVersion;
        try {
            updatedBookVersion = validateAndGetParams(req);
        } catch (ServiceException | DaoException e) {
            session.setAttribute(ServletAttributes.USER_ERROR, e.getMessage());
            return errorPage;
        }

        updatedBookVersion.setId(oldBookVersion.getId());
        session.setAttribute(ATTR_PROCEED_BOOK, updatedBookVersion); // for not loosing user edition
        logger.trace("oldBookVersion={}", oldBookVersion);

        BookStat oldStat = oldBookVersion.getBookStat();

        long newTotal = updatedBookVersion.getBookStat().getTotal();
        long booksUsersAreHolding = oldStat.getTotal() - oldStat.getInStock();
        long newInStock = newTotal - booksUsersAreHolding;

        if (newInStock < 0) {
            session.setAttribute(ServletAttributes.USER_ERROR,
                    "error.total.is.less.than.user.subscriptions");
            return errorPage;
        }

        if (newInStock < oldStat.getReserved()) {
            session.setAttribute(ServletAttributes.USER_ERROR,
                    "error.stock.less.than.reserved.books.num");
            return errorPage;
        }

        oldStat.setTotal(newTotal);
        oldStat.setInStock(newInStock);
        updatedBookVersion.setBookStat(oldStat);
        logger.trace("updatedBookVersion={}", updatedBookVersion);

        BookDao dao = daoFactory.getBookDao();
        try {
            dao.update(updatedBookVersion);
        } catch (DaoException e) {
            session.setAttribute(ServletAttributes.USER_ERROR,
                    e.getMessage());
            return errorPage;
        }

        session.removeAttribute(ATTR_PROCEED_BOOK);
        //session.removeAttribute(ATTR_SAVED_USER_INPUT);
        logger.debug(END_MSG);

        return nextPageLogic(req);
    }

    private static String nextPageLogic(HttpServletRequest req) throws ServiceException {
        SafeSession safeSession = new SafeSession(req.getSession());
        String page = safeSession.get(ATTR_BOOK_SEARCH_LINK).convert(String.class::cast);

        if (page == null) {
            logger.trace("previous search is null, set page to home={}", Pages.HOME);
            page = Pages.HOME;
        }

        return page;
    }

    /**
     * Deletes the book by id specified in request. No user error is possible here, only programmer's.
     *
     * @return next page to be seen by user
     * @throws ServiceException only in case of programmer error
     * @throws DaoException in case of dao error
     */
    public static String delete(HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        SafeRequest safeReq = new SafeRequest(req);
        long id = safeReq.get("id").notNull().convert(Long::parseLong);

        BookDao dao = daoFactory.getBookDao();
        dao.delete(id);

        logger.debug(END_MSG);
        return nextPageLogic(req);
    }
}
