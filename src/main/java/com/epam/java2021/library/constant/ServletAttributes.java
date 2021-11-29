package com.epam.java2021.library.constant;

public class ServletAttributes {
    private ServletAttributes() {}

    // Servlet only
    public static final String PAGE = "page";
    public static final String USER = "user";
    public static final String ROLE = "role";

    // jsp also
    public static final String SERVICE_ERROR = "serviceError";
    public static final String USER_ERROR = "userError";
    public static final String PASS = "password";
    public static final String EMAIL = "email";
    public static final String ATTR_BOOKINGS = "bookings";
    public static final String BOOKING_ID = "bookingID";
    public static final String NOT_FOUND = "notFound";
    public static final String APP_ROLES = "appRoles";
    public static final String COMMAND = "command";
    public static final String PLAIN_TEXT = "plainText";
    public static final String PAGES_NUM = "pagesNum";
    public static final String ATTR_SEARCH_LINK = "searchLink";

    public static final String ATTR_BOOKS = "books";
    public static final String ATTR_USERS = "users";
    public static final String ATTR_AUTHORS = "authors";

    public static final String SUPPORTED_LANGUAGES = "langs";
    public static final String LANG = "lang";
    public static final String DEFAULT_LANG = "defaultLang";
    public static final String URL = "url";

    public static final String LOG__START_MSG = "start";
    public static final String LOG__END_MSG = "end";

    public static final String ATTR_PROCEED_BOOK = "proceedBook";
    public static final String ATTR_PROCEED_USER = "proceedUser";
    public static final String ATTR_PROCEED_AUTHOR = "proceedAuthor";

    public static final String JSP_AUTHOR_FORM_NAME = "name";
    public static final String JSP_AUTHOR_FORM_PRIMARY_LANG = "primaryLang";
    public static final String JSP_FORM_ATTR_ID = "id";


    public static final String ERR_WRONG_LANG_CODE = "error.wrong.language.code";
}