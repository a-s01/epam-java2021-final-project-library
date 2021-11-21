package com.epam.java2021.library.dao.impl.mysql.util;

import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class SearchSortColumns {
    private final Set<String> columns = new HashSet<>();
    private final Logger logger;

    public SearchSortColumns(Logger logger, String... columns) {
        this.logger = logger;
        for (String s: columns) {
            this.columns.add(s);
        }
    }

    public void check(String s, String action) throws ServiceException {
        if (!columns.contains(s)) {
            logger.error("{} forbidden for column {}", action, s);
            throw new ServiceException(action + " forbidden for column " + s);
        }
    }
}
