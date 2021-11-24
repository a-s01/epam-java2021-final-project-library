package com.epam.java2021.library.dao.impl.mysql.util;

import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class SearchSortColumn {
    private static final Logger logger = LogManager.getLogger(SearchSortColumn.class);
    public static final String SEARCH = "Search";
    public static final String SORT = "Sort";

    private final Set<String> columns = new HashSet<>();

    public SearchSortColumn(String... columns) {
        for (String col: columns) {
            this.columns.add(col);
        }
    }

    public void check(String s, String action) throws ServiceException {
        if (!columns.contains(s)) {
            logger.error("{} forbidden for column {}", action, s);
            throw new ServiceException(action + " forbidden for column " + s);
        }
    }
}
