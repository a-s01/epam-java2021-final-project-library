package com.epam.java2021.library.service;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.dao.SuperDao;
import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.util.SafeRequest;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.java2021.library.constant.ServletAttributes.*;

public class CommonLogic {

    private CommonLogic() {}

    public static <E extends Entity> String find(HttpSession session, HttpServletRequest req, Logger logger, SuperDao<E> dao, String reqAttribute, String desiredPage) throws ServiceException {
        SafeRequest safeReq = new SafeRequest(req);
        String query = safeReq.get("query").escape().convert();
        String searchBy = safeReq.get("searchBy").notEmpty().escape().convert().toLowerCase();
        String sortBy = safeReq.get("sortBy").notEmpty().escape().convert().toLowerCase();
        int num = safeReq.get("num").notNull().convert(Integer::parseInt);

        int pageNum = 1;
        try {
            pageNum = safeReq.get("page").notEmpty().convert(Integer::parseInt);
        } catch (ServiceException e) {
            logger.trace("page is empty, set it to 1");
        }

        if (num == 0) {
            throw new ServiceException("error.amount.cannot.be.zero");
        }

        logger.trace("query={}, searchBy={}, sortBy={}, num={}, pageNum={}",
                query, searchBy, sortBy, num, pageNum);

        List<E> list = null;
        String page;
        int totalCount = -1;
        try {
            if (pageNum == 1) {
                session.removeAttribute(PAGES_NUM);
                totalCount = dao.findByPatternCount(query, searchBy);
                session.setAttribute(PAGES_NUM, Math.ceil(1.0 * totalCount / num));
                logger.trace("totalCount={}", totalCount);
            }
            if ((pageNum == 1 && totalCount > 0) || pageNum > 1) {
                list = dao.findByPattern(query, searchBy, sortBy, num, pageNum);
            }
            page = desiredPage;
        } catch (DaoException | ServiceException e) {
            req.setAttribute(SERVICE_ERROR, e.getMessage());
            page = Pages.ERROR;
        }

        // books = null if totalCount < 1
        if (list == null || list.isEmpty()) {
            logger.trace("{} not found", reqAttribute);
            req.setAttribute(NOT_FOUND, "error.not.found");
        }

        req.setAttribute(reqAttribute, list);
        session.setAttribute(ATTR_SEARCH_LINK, req.getRequestURI()
                + '?' + req.getQueryString().replace("&page=" + pageNum, ""));
        logger.debug("end");
        return page;
    }
}
