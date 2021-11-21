package com.epam.java2021.library.constant;

public class ServletAttributes {
    private ServletAttributes() {}

    // Servlet only
    public static final String PAGE = "page";
//??
    public static final String USER = "user";
    public static final String ROLE = "role";

    // jsp also
    public static final String SERVICE_ERROR = "serviceError";
    public static final String USER_ERROR = "userError";
    public static final String REG_PASS = "passwordReg";
    public static final String REG_EMAIL = "emailReg";
    public static final String LOGIN_PAGE_ERROR_MSG = "errorErrorMsg";
    public static final String ERROR_PAGE_ERROR_MSG = "loginErrorMsg";
    public static final String REG_PAGE_ERROR_MSG = "regErrorMsg";
    public static final String BOOK_PAGE_ERROR_MSG = "bookNotFound";
    public static final String BOOKS_IN_BOOKING = "booksInBooking";
    public static final String BOOKING_ID = "bookingID";
    public static final String NOT_FOUND = "notFound";
    public static final String APP_ROLES = "appRoles";
    public static final String REQ_TYPE = "reqType";
    public static final String COMMAND = "command";
    public static final String PLAIN_TEXT = "plainText";
    public static final String PAGES_NUM = "pagesNum";
    public static final String SEARCH_LINK = "searchLink";
    public static final String CUR_PAGE = "curPage";
}