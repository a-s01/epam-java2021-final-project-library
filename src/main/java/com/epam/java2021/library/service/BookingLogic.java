package com.epam.java2021.library.service;

import com.epam.java2021.library.constant.Pages;
import com.epam.java2021.library.dao.AbstractSuperDao;
import com.epam.java2021.library.dao.BookDao;
import com.epam.java2021.library.dao.BookingDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.entity.impl.BookStat;
import com.epam.java2021.library.entity.impl.Booking;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.util.SafeRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import static com.epam.java2021.library.constant.Common.END_MSG;
import static com.epam.java2021.library.constant.Common.START_MSG;
import static com.epam.java2021.library.constant.ServletAttributes.*;

/**
 * USER can:
 *  * addBook to NEW booking, deleteBook from NEW booking, book (NEW), cancel (NEW and BOOKED), (//2.0 renew)
 *  * (all NEW bookings are in session (set cookie for later)) ( session ? id in cookie ? check in db for new ? new booking
 *  * see all bookings: request to db, get all booking, c:out (later)
 *  * see books in subscription: request to db, get all books in booking with state DELIVERED and LOCATION = user
 * -> c:out
 *  * see books in specific booking: get bookingID, get books in booking
 *
 * LIBRARIAN can:
 *  * deliver (BOOKED), done (DELIVERED), cancel(BOOKED) booking for a user (by bookingID in req)
 *  * search for booking: user(email), booking(state) book(title, author, isbn)
 */

public class BookingLogic {
    private static final Logger logger = LogManager.getLogger(BookingLogic.class);
    private static final IDaoFactoryImpl daoFactory = DaoFactoryCreator.getDefaultFactory().getDefaultImpl();
    public static final String BOOKING_TRACE = "booking={}";

    private BookingLogic() {}

    private static Booking findBooking(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        return findBooking(session, req, false);
    }

    private static Booking findBooking(HttpSession session, HttpServletRequest req, boolean create) throws ServiceException, DaoException {
        logger.debug(START_MSG);
        logger.trace("sessionID={}, reqURI={}", session.getId(), req.getRequestURI());
        User u = (User) session.getAttribute("user");

        Booking booking = null;
        if (u.getRole().equals(User.Role.USER)) {
            booking = findBookingForUser(session, u, create);
        }

        if (u.getRole().equals(User.Role.LIBRARIAN)) {
            String bookingID = req.getParameter(BOOKING_ID);
            if (bookingID != null) {
                logger.debug("found {} in request", BOOKING_ID);
                logger.trace("{}={}", BOOKING_ID, bookingID);
                BookingDao dao = daoFactory.getBookingDao();
                booking = dao.read(Long.parseLong(bookingID));
            }
            if (booking == null) {
                throw new ServiceException("error.booking.not.found");
            }
        }
        logger.trace(BOOKING_TRACE, booking);
        logger.debug(END_MSG);
        return booking;
    }

    /**
     * This will definitely return booking in case of create=true;
     */
    private static Booking findBookingForUser(HttpSession session, User u, boolean create) {
        logger.debug("findBooking request for USER role init...");

        Booking booking = (Booking) session.getAttribute(BOOKING);
        if (booking != null) {
            logger.debug("found booking in session");
            logger.trace(BOOKING_TRACE, booking);
            return booking;
        }

        logger.debug("booking was not found");
        if (create) {
            logger.debug("create new one requested");
            Booking.Builder builder = new Booking.Builder();
            builder.setUser(u);
            booking = builder.build();
            session.setAttribute(BOOKING, booking);
            logger.trace(BOOKING_TRACE, booking);
        }

        return booking;
    }

    /**
     * 2 cases: listBooks in particular booking (should have bookingID then) or listBooks on subscription
     */
    public static String listBookInSubscription(HttpSession session, HttpServletRequest req) throws DaoException {
        logger.debug(START_MSG);

        User user = (User) session.getAttribute("user");
        logger.trace("user={}", user);

        logger.debug("looking for user bookings in DB...");
        BookingDao dao = daoFactory.getBookingDao();
        List<Booking> bookings = dao.findDeliveredByUserID(user.getId());

        session.setAttribute(ATTR_BOOKINGS, bookings);
        logger.trace("set session attribute {}={}", ATTR_BOOKINGS, bookings);
        logger.debug(END_MSG);
        return Pages.MY_BOOKS;
    }

    public static String find(HttpSession session, HttpServletRequest req) throws ServiceException {
        // /booking?command=find - (mb later user) or all (for librarian) bookings
        // only for librarian now
        logger.debug(START_MSG);
        BookingDao dao = daoFactory.getBookingDao();
        return CommonLogic.find(session, req, dao, ATTR_BOOKINGS, BOOKING, Pages.BOOKING);
    }

    public static String basket(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        //  /booking?command=search&type=basket - the latest user booking
        logger.debug(START_MSG);
        User u = (User) session.getAttribute("user");
        if (!u.getRole().equals(User.Role.USER)) {
            throw new ServiceException("error.resource.forbidden");
        }

        Booking currentBooking = findBookingForUser(session, u, false);
        session.setAttribute(ATTR_PROCEED_BOOKING, currentBooking);
        logger.trace("set {} to {}", ATTR_PROCEED_BOOKING, currentBooking);

        BookingDao bookingDao = daoFactory.getBookingDao();

        List<Booking> bookings = bookingDao.findBy(u.getEmail(), "email");
        // we don't want current booking to be repeated
        bookings.remove(currentBooking);
        bookings.sort(Comparator.comparing(Booking::getState));

        session.setAttribute(ATTR_BOOKINGS, bookings);
        logger.trace("set {} to {}", ATTR_BOOKINGS, bookings);

        logger.debug(END_MSG);
        return Pages.BASKET;
    }

    public static String addBook(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        //Booking booking, long id
        Booking booking = findBooking(session, req, true); // booking != null guarantied
        if (req.getParameter("id") == null) {
            throw new ServiceException("error.no.id.in.request");
        }

        long id = Long.parseLong(req.getParameter("id"));

        logger.trace("addBook request: booking={}, id={}", booking, id);
        if (booking.getState() != Booking.State.NEW) {
            throw new ServiceException("error.add.book.to.not.new.booking");
        }

        BookDao bookDao = daoFactory.getBookDao();
        Book book = bookDao.read(id);
        if (book == null) {
            throw new ServiceException("error.not.found");
        }
        logger.trace("book={}", book);

        if (!booking.getBooks().contains(book)) {
            booking.addBook(book);
            logger.debug("Book was added");
        } else {
            logger.debug("Book already exists in booking");
        }
        req.setAttribute(ATTR_OUTPUT, String.valueOf(booking.getBooks().size()));
        logger.debug(END_MSG);
        return null;
    }

    public static String removeBook(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        // Booking booking, long id
        Booking booking = findBooking(session, req);
        if (req.getParameter("id") == null) {
            throw new ServiceException("error.no.id.in.request");
        }

        long id = Long.parseLong(req.getParameter("id"));

        logger.trace("removeBook request: booking={}, id={}", booking, id);

        if (booking == null) {
            throw new ServiceException("error.add.some.book");
        }

        if (booking.getState() != Booking.State.NEW) {
            throw new ServiceException("error.remove.illegal.state");
        }

        AbstractSuperDao<Book> bookDao = daoFactory.getBookDao();
        Book book = bookDao.read(id);
        booking.removeBook(book);

        logger.debug(END_MSG);
        return Pages.BASKET;
    }

    public static String cancel(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);
        // Booking booking
        Booking booking = findBooking(session, req);
        User user = (User) session.getAttribute(USER);
        
        if (user == null) {
            throw new ServiceException("error.resource.forbidden");
        }

        String page = user.getRole() == User.Role.LIBRARIAN ? Pages.BOOKING : Pages.BASKET;
        logger.trace("cancel request: booking={}", booking);

        if (booking == null) {
            throw new ServiceException("error.cancel.null.booking");
        }

        Booking.State state = booking.getState();
        if (state != Booking.State.NEW && state != Booking.State.BOOKED) {
            throw new ServiceException("error.cancel.illegal.state");
        }

        if (state == Booking.State.NEW) {
            // nothing was added to DB yet, so just delete and forget
            booking.setBooks(new ArrayList<>());
            return page;
        }

        // so state was BOOKED and written to DB
        for (Book book: booking.getBooks()) {
            BookStat bookStat = book.getBookStat();
            bookStat.setReserved(bookStat.getReserved() - 1);
        }
        booking.setState(Booking.State.CANCELED);
        booking.setModified(Calendar.getInstance());

        daoFactory.getBookingDao().update(booking);
        req.setAttribute(PAGE, Pages.BASKET);
        
        logger.debug(END_MSG);
        return page;
    }

    public static String book(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);
        // Booking booking
        Booking booking = findBooking(session, req);

        logger.trace("book request: booking={}", booking);

        if (booking == null) {
            throw new ServiceException("error.booked.null.booking");
        }

        Booking.State state = booking.getState();
        if (state != Booking.State.NEW) {
            throw new ServiceException("error.booked.illegal.state");
        }

        booking.setState(Booking.State.BOOKED);
        booking.setModified(Calendar.getInstance());

        daoFactory.getBookingDao().create(booking);
        
        logger.debug(END_MSG);
        return Pages.BASKET;
    }

    public static String deliver(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        // Booking booking, boolean subscription
        Booking booking = findBooking(session, req);

        SafeRequest safeRequest = new SafeRequest(req);
        boolean subscription = safeRequest.get("subscription").convert(Boolean::parseBoolean);

        logger.trace("booking={}, subscription={}", booking, subscription);

        if (booking == null) {
            throw new ServiceException("error.deliver.null.booking");
        }

        if (booking.getState() != Booking.State.BOOKED) {
            throw new ServiceException("error.deliver.illegal.state");
        }

        booking.setState(Booking.State.DELIVERED);
        if (subscription) {
            booking.setLocated(Booking.Place.USER);
            logger.trace("deliver to user");
        }

        for (Book book: booking.getBooks()) {
            BookStat stat = book.getBookStat();
            stat.setReserved(stat.getReserved() - 1);
            stat.setInStock(stat.getInStock() - 1);
        }
        booking.setModified(Calendar.getInstance());

        BookingDao dao = daoFactory.getBookingDao();
        dao.update(booking);
        
        logger.debug(END_MSG);
        return Pages.BOOKING;
    }

    public static String done(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        // Booking booking
        Booking booking = findBooking(session, req);

        logger.trace("done request: booking={}", booking);

        if (booking == null) {
            throw new ServiceException("error.done.null.booking");
        }

        if (booking.getState() != Booking.State.DELIVERED) {
            throw new ServiceException("error.done.illegal.state");
        }

        booking.setState(Booking.State.DONE);
        for (Book book: booking.getBooks()) {
            BookStat stat = book.getBookStat();
            stat.setInStock(stat.getInStock() + 1);
        }
        booking.setModified(Calendar.getInstance());

        daoFactory.getBookingDao().update(booking);
        
        logger.debug(END_MSG);
        return Pages.BOOKING;
    }
}
