package com.epam.java2021.library.controller.servlet;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.constant.ServletAttributes;
import com.epam.java2021.library.dao.SuperDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/findBook")
public class FindBook extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(FindBook.class);
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("query");
        String searchBy = req.getParameter("searchBy");
        String sortBy = req.getParameter("sortBy");
        String num = req.getParameter("num");
        String pageNum = req.getParameter("page");
        // TODO sure in jsp
        if (pageNum == null) {
            pageNum = "0";
        }

        logger.trace("Request for find book: query={}, searchBy={}, sortBy={}, num={}, pageNum={}",
                query, searchBy, sortBy, num, pageNum);

        List<Book> books = null;
        String page;
        try {
            SuperDao<Book> dao = DaoFactoryCreator.getDefaultFactory().getDefaultImpl().getBookDao();
            books = dao.findByPattern(query, searchBy.toLowerCase(), sortBy.toLowerCase(),
                    Integer.valueOf(num), Integer.valueOf(pageNum));
            page = Pages.HOME;
        } catch (DaoException | ServiceException e) {
            req.setAttribute(ServletAttributes.SERVICE_ERROR, e.getMessage());
            page = Pages.ERROR;
        }

        if (books.isEmpty()) {
            logger.trace("Books not found");
            req.setAttribute(ServletAttributes.BOOK_PAGE_ERROR_MSG, "Nothing was found");
        }

        req.setAttribute("books", books);
        req.getRequestDispatcher(page).forward(req, resp);
    }
}
