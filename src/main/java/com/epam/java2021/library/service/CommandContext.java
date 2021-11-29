package com.epam.java2021.library.service;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.util.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class CommandContext {
    private static final Logger logger = LogManager.getLogger(CommandContext.class);
    private static final Map<String, AuthContext> commands = new HashMap<>();

    private CommandContext() {}

    static {
        commands.put("book.find", new AuthContext(BookLogic::find, User.Role.UNKNOWN));
        commands.put("book.add", new AuthContext(BookLogic::add, User.Role.ADMIN));
        commands.put("book.edit", new AuthContext(BookLogic::edit, User.Role.ADMIN));
        commands.put("book.delete", new AuthContext(BookLogic::delete, User.Role.ADMIN));
        commands.put("booking.addBook", new AuthContext(BookingLogic::addBook, User.Role.USER, User.Role.LIBRARIAN));
        commands.put("booking.removeBook", new AuthContext(BookingLogic::removeBook, User.Role.USER, User.Role.LIBRARIAN));
        commands.put("booking.listBooksInSubscription", new AuthContext(BookingLogic::listBookInSubscription, User.Role.USER));
        commands.put("booking.find", new AuthContext(BookingLogic::find, User.Role.USER, User.Role.LIBRARIAN));
        commands.put("booking.basket", new AuthContext(BookingLogic::basket, User.Role.USER));
        commands.put("booking.book", new AuthContext(BookingLogic::book, User.Role.USER, User.Role.LIBRARIAN));
        commands.put("booking.deliver", new AuthContext(BookingLogic::deliver, User.Role.LIBRARIAN));
        commands.put("booking.cancel", new AuthContext(BookingLogic::cancel, User.Role.USER, User.Role.LIBRARIAN));
        commands.put("booking.done", new AuthContext(BookingLogic::done, User.Role.LIBRARIAN));
        commands.put("user.login", new AuthContext(UserLogic::login, User.Role.UNKNOWN));
        commands.put("user.find", new AuthContext(UserLogic::find, User.Role.ADMIN));
        commands.put("user.add", new AuthContext(UserLogic::add, User.Role.UNKNOWN, User.Role.ADMIN));
        commands.put("user.logout", new AuthContext(UserLogic::logout, User.Role.USER, User.Role.LIBRARIAN, User.Role.ADMIN));
        commands.put("user.edit", new AuthContext(UserLogic::edit, User.Role.ADMIN));
        commands.put("user.delete", new AuthContext(UserLogic::delete, User.Role.ADMIN));
        commands.put("user.setLang", new AuthContext(UserLogic::setLang, User.Role.UNKNOWN));
        commands.put("author.add", new AuthContext(AuthorLogic::add, User.Role.ADMIN));
        commands.put("author.edit", new AuthContext(AuthorLogic::edit, User.Role.ADMIN));
        commands.put("author.delete", new AuthContext(AuthorLogic::delete, User.Role.ADMIN));
        commands.put("author.find", new AuthContext(AuthorLogic::find, User.Role.ADMIN));

        commands.put(Pages.ERROR, new AuthContext(null, User.Role.UNKNOWN));
        commands.put(Pages.MY_BOOKS, new AuthContext(null, User.Role.USER));
        commands.put(Pages.BASKET, new AuthContext(null, User.Role.USER));
        commands.put(Pages.HOME, new AuthContext(null, User.Role.UNKNOWN));
        commands.put(Pages.ROOT, new AuthContext(null, User.Role.UNKNOWN));
        commands.put(Pages.LOGIN, new AuthContext(null, User.Role.UNKNOWN));
        commands.put(Pages.REGISTER, new AuthContext(null, User.Role.UNKNOWN, User.Role.ADMIN));
        commands.put(Pages.BOOKING, new AuthContext(null, User.Role.LIBRARIAN));
        commands.put(Pages.USERS, new AuthContext(null, User.Role.ADMIN));
        commands.put(Pages.BOOK_EDIT, new AuthContext(null, User.Role.ADMIN));
        commands.put(Pages.JS, new AuthContext(null, User.Role.UNKNOWN));
        commands.put(Pages.AUTHORS, new AuthContext(null, User.Role.ADMIN));
        commands.put(Pages.AUTHOR_EDIT, new AuthContext(null, User.Role.ADMIN));
    }
    
    private static class AuthContext {
        private final Command command;
        private final Set<User.Role> roles;

        public AuthContext(Command command, User.Role... roles) {
            this.command = command;
            
            Set<User.Role> set = new HashSet<>();
            Collections.addAll(set, roles);
            this.roles = set;
        }

        public Command getCommand() {
            return command;
        }
        
        public boolean isAllowed(User.Role role) {
            if (roles.contains(role)) {
                return true;
            }

            return (roles.size() == 1) && roles.contains(User.Role.UNKNOWN);
        }
    }
    
    public static Command getCommand(String s) throws ServiceException {
        logger.trace("commandStr={}", s);
        checkCommandString(s);
        return commands.get(s).getCommand();
    }

    public static boolean isAllowed(String s, User.Role role) throws ServiceException {
        logger.trace("parameters: commandStr={}, role={}", s, role);

        checkCommandString(s);
        return commands.get(s).isAllowed(role);
    }

    private static void checkCommandString(String s) throws ServiceException {
        if (s == null || commands.get(s) == null) {
            throw new ServiceException("Illegal command request");
        }
    }
}
