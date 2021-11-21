package com.epam.java2021.library.service;

import com.epam.java2021.library.constant.Pages;
import static com.epam.java2021.library.constant.ServletAttributes.*;
import com.epam.java2021.library.dao.BookDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.util.SafeRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class BookLogic {
    private static final Logger logger = LogManager.getLogger(BookLogic.class);

    private BookLogic() {}

    public static String find(HttpSession session, HttpServletRequest req) throws ServiceException {
        logger.debug("start");
        SafeRequest safeReq = new SafeRequest(req);
        String query = safeReq.getString("query");
        String searchBy = safeReq.getNotEmptyString("searchBy").toLowerCase();
        String sortBy = safeReq.getNotEmptyString("sortBy").toLowerCase();
        int num = safeReq.getNotNullParameter("num", Integer::parseInt);
        int pageNum = safeReq.getNotNullParameter("page", Integer::parseInt);

        logger.trace("query={}, searchBy={}, sortBy={}, num={}, pageNum={}",
                query, searchBy, sortBy, num, pageNum);

        List<Book> books = null;
        String page;
        int totalCount = -1;
        try {
            BookDao dao = DaoFactoryCreator.getDefaultFactory().getDefaultImpl().getBookDao();
            if (pageNum == 0) {
                session.removeAttribute(TOTAL_COUNT);
                totalCount = dao.findByPatternCount(query, searchBy, sortBy);
                session.setAttribute(TOTAL_COUNT, totalCount);
                logger.trace("totalCount={}", totalCount);
            }
            if ((pageNum == 0 && totalCount > 0) || pageNum > 0) {
                books = dao.findByPattern(query, searchBy, sortBy, num, pageNum);
            }
            page = Pages.HOME;
        } catch (DaoException | ServiceException e) {
            req.setAttribute(SERVICE_ERROR, e.getMessage());
            page = Pages.ERROR;
        }

        // books = null if totalCount < 1
        if (books == null || books.isEmpty()) {
            logger.trace("Books not found");
            req.setAttribute(NOT_FOUND, "Nothing was found");
        }

        req.setAttribute("books", books);
        logger.debug("end");
        return page;
    }

    public static String add(HttpSession session, HttpServletRequest req) {
        String page = null;
        return page;
    }

    public static String edit(HttpSession session, HttpServletRequest req) {
        String page = null;
        return page;
    }

    public static String delete(HttpSession session, HttpServletRequest req) {
        String page = null;
        return page;
    }
}
