package com.epam.java2021.library.service;

import com.epam.java2021.library.constant.Pages;
import static com.epam.java2021.library.constant.ServletAttributes.*;

import com.epam.java2021.library.dao.BookDao;
import com.epam.java2021.library.dao.BookingDao;
import com.epam.java2021.library.dao.SuperDao;
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
import java.util.List;
import java.util.stream.Collectors;

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

// TODO calc fine!!!
// TODO make booking expire
public class BookingLogic {
    private static final Logger logger = LogManager.getLogger(BookingLogic.class);
    private static final IDaoFactoryImpl daoFactory = DaoFactoryCreator.getDefaultFactory().getDefaultImpl();

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
            booking = findBookingForUser(session, req, u, create);
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
                throw new ServiceException("Unable to find booking");
            }
        }
        logger.trace("booking={}", booking);
        logger.debug(END_MSG);
        return booking;
    }

    /**
     * This will definitely return booking in case of create=true;
     */
    private static Booking findBookingForUser(HttpSession session, HttpServletRequest req, User u, boolean create)
            throws DaoException, ServiceException {
        logger.debug("findBooking request for USER role init...");

        Booking booking = (Booking) session.getAttribute("booking");
        if (booking != null) {
            logger.debug("found booking in session");
            logger.trace("booking={}", booking);
            return booking;
        }

        logger.debug("booking was not found");
        if (create) {
            logger.debug("create new one requested");
            Booking.Builder builder = new Booking.Builder();
            builder.setUser(u);
            booking = builder.build();
            session.setAttribute("booking", booking);
            logger.trace("booking={}", booking);
        }

        return booking;
    }

    /**
     * 2 cases: listBooks in particular booking (should have bookingID then) or listBooks on subscription
     */
    public static String listBooks(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug("init...");

        boolean subscription = Boolean.parseBoolean(req.getParameter("subscription"));
        User u = (User) session.getAttribute("user");
        logger.trace("subscription={}, user={}", subscription, u);

        List<Book> books;
        String page;
        if (subscription) {
            books = getBooksInSubscription(req, u);
            page = Pages.MY_BOOKS;
        } else {
            // or we have bookingID
            books = getBooksForBookingID(req);
            page = Pages.BOOKING;
        }

        logger.trace("set 'booksInBooking' session attribute to: books={}", books);
        session.setAttribute(BOOKS_IN_BOOKING, books);
        logger.debug("finished");
        return page;
    }

    private static List<Book> getBooksForBookingID(HttpServletRequest req) throws DaoException, ServiceException {
        logger.debug("getBooksForBookingID init..");

        String bIDStr = req.getParameter(BOOKING_ID);
        logger.trace("{}={}", BOOKING_ID, bIDStr);

        long bookingID;
        try {
            bookingID = Long.parseLong(bIDStr);
        } catch (NumberFormatException e) {
            throw new ServiceException(BOOKING_ID + " should be a long number");
        }

        List<Book> books = null;
        if (bookingID != 0) {
            logger.debug("Found {} in session: id={}", BOOKING_ID, bookingID);
            BookingDao dao = daoFactory.getBookingDao();
            Booking booking = dao.read(bookingID);
            if (booking != null) {
                books = booking.getBooks();
            }
        }

        logger.debug("getBooksForBookingID finished");
        return books;
    }

    private static List<Book> getBooksInSubscription(HttpServletRequest req, User u) throws DaoException, ServiceException {
        logger.debug("getBooksInSubscription init");
        List<Book> books;
        if (u.getRole().equals(User.Role.USER)) {
            logger.debug("looking for user bookings in DB...");
            BookingDao dao = daoFactory.getBookingDao();
            List<Booking> bookings = dao.findDeliveredByUserID(u.getId());
            books = bookings.stream()
                                .map(Booking::getBooks)
                                .distinct()
                                .flatMap(List::stream)
                                .collect(Collectors.toList());
            req.setAttribute(PAGE, Pages.MY_BOOKS);
            logger.debug("Set page attribute");
            logger.trace("{}={}", PAGE, Pages.MY_BOOKS);
        } else {
            throw new ServiceException("Isn't supported for LIBRARIAN yet");
        }

        logger.debug("end");
        return books;
    }

    public static String find(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        // /booking?command=find - (mb later user) or all (for librarian) bookings
        // only for librarian now
        logger.debug("start");
        BookingDao dao = daoFactory.getBookingDao();
        return CommonLogic.find(session, req, logger, dao, "bookings", Pages.BOOKING);
    }

    public static String basket(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        //  /booking?command=search&type=basket - user latest booking
        logger.debug("start");
        User u = (User) session.getAttribute("user");
        if (!u.getRole().equals(User.Role.USER)) {
            throw new ServiceException("Is allowed only for USER");
        }

        Booking booking = findBookingForUser(session, req, u, false);
        session.setAttribute("booking", booking);

        logger.debug("end");
        return Pages.BASKET;
    }

    public static String addBook(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug("start");

        //Booking booking, long id
        Booking booking = findBooking(session, req, true); // booking != null guarantied
        if (req.getParameter("id") == null) {
            throw new ServiceException("Unable to locate id in request");
        }

        long id = Long.parseLong(req.getParameter("id"));

        logger.trace("addBook request: booking={}, id={}", booking, id);
        if (booking.getState() != Booking.State.NEW) {
            throw new ServiceException("Cannot add books to not NEW booking");
        }

        BookDao bookDao = daoFactory.getBookDao();
        Book book = bookDao.read(id);
        if (book == null) {
            throw new ServiceException("Book not found");
        }
        logger.trace("book={}", book);

        BookStat bookStat = book.getBookStat();
        if (!booking.getBooks().contains(book)) {
            if ((bookStat.getInStock() - bookStat.getReserved()) > 0) {
                booking.getBooks().add(book);
                logger.debug("Book was added");
            } else {
                throw new ServiceException("No free books for now, unable to reserve");
            }
        } else {
            logger.debug("Book already exists in booking");
        }
        req.setAttribute(PLAIN_TEXT, String.valueOf(booking.getBooks().size()));
        logger.debug("end");
        return null;
    }

    public static String removeBook(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug("start");

        // Booking booking, long id
        Booking booking = findBooking(session, req);
        if (req.getParameter("id") == null) {
            throw new ServiceException("Unable to locate id in request");
        }

        long id = Long.parseLong(req.getParameter("id"));

        logger.trace("removeBook request: booking={}, id={}", booking, id);

        if (booking == null) {
            throw new ServiceException("You should add some book first");
        }

        if (booking.getState() != Booking.State.NEW) {
            throw new ServiceException("Cannot remove books from not NEW booking");
        }

        SuperDao<Book> bookDao = daoFactory.getBookDao();
        Book book = bookDao.read(id);
        booking.getBooks().remove(book);

        logger.debug("end");
        return Pages.BASKET;
    }

    public static String cancel(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug("start");
        // Booking booking
        Booking booking = findBooking(session, req);

        logger.trace("cancel request: booking={}", booking);

        if (booking == null) {
            throw new ServiceException("Cannot cancel null booking");
        }

        Booking.State state = booking.getState();
        if (state != Booking.State.NEW && state != Booking.State.BOOKED) {
            throw new ServiceException("Cannot move booking to CANCELED state from any state except NEW and BOOKED");
        }

        if (state == Booking.State.NEW) {
            // nothing was added to DB yet, so just delete and forget
            booking.setBooks(null);
            return Pages.BOOKING; // TODO to think about
        }

        // so state was BOOKED and written to DB
        for (Book book: booking.getBooks()) {
            BookStat bookStat = book.getBookStat();
            bookStat.setReserved(bookStat.getReserved() - 1);
        }
        booking.setState(Booking.State.CANCELED);

        daoFactory.getBookingDao().update(booking);
        req.setAttribute(PAGE, Pages.BASKET);

        updateBookingInSession(session, booking);

        logger.debug("end");
        return Pages.BOOKING; // TODO to think about
    }

    public static String book(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug("start");
        // Booking booking
        Booking booking = findBooking(session, req);

        logger.trace("book request: booking={}", booking);

        if (booking == null) {
            throw new ServiceException("Cannot move to BOOKED state null booking");
        }

        Booking.State state = booking.getState();
        if (state != Booking.State.NEW) {
            throw new ServiceException("Cannot move booking to BOOKED state from any state except of NEW");
        }

        booking.setState(Booking.State.BOOKED);
        for (Book book: booking.getBooks()) {
            BookStat bookStat = book.getBookStat();
            bookStat.setReserved(bookStat.getReserved() + 1);
            bookStat.setTimesWasBooked(bookStat.getTimesWasBooked() + 1);
        }

        daoFactory.getBookingDao().create(booking);

        updateBookingInSession(session, booking);

        logger.debug("end");
        return Pages.BASKET;
    }

    public static String deliver(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug("start");

        // Booking booking, boolean subscription
        Booking booking = findBooking(session, req);

        SafeRequest safeRequest = new SafeRequest(req);
        boolean subscription = safeRequest.get("subscription").convert(Boolean::parseBoolean);

        logger.trace("booking={}, subscription={}", booking, subscription);

        if (booking == null) {
            throw new ServiceException("Cannot move to DELIVER state null booking");
        }

        if (booking.getState() != Booking.State.BOOKED) {
            throw new ServiceException("Cannot move booking to DELIVERED state from any state except of BOOKED");
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

        BookingDao dao = daoFactory.getBookingDao();
        dao.update(booking);

        updateBookingInSession(session, booking);

        logger.debug("end");
        return Pages.BOOKING;
    }

    public static String done(HttpSession session, HttpServletRequest req) throws ServiceException, DaoException {
        logger.debug("start");

        // Booking booking
        Booking booking = findBooking(session, req);

        logger.trace("done request: booking={}", booking);

        if (booking == null) {
            throw new ServiceException("Cannot move to DONE state null booking");
        }

        if (booking.getState() != Booking.State.DELIVERED) {
            throw new ServiceException("Cannot move booking to DONE state from any state except DELIVERED");
        }

        booking.setState(Booking.State.DONE);
        for (Book book: booking.getBooks()) {
            BookStat stat = book.getBookStat();
            stat.setInStock(stat.getInStock() + 1);
        }

        daoFactory.getBookingDao().update(booking);

        updateBookingInSession(session, booking);

        logger.debug("end");
        return Pages.BOOKING;
    }

    private static void updateBookingInSession(HttpSession session, Booking booking) {
        /*logger.debug(START_MSG);
        SafeSession safeSession = new SafeSession(session);
        List<Booking> bookings = safeSession.getParameter("bookings", List.class::cast);
        if (bookings != null) {
            int i = 0;
            for (Booking b: bookings) {
                if (b.getId() == booking.getId()) {
                    bookings.remove(b);
                    bookings.add(i, booking);
                }
                i++;
            }
        }
        logger.debug(END_MSG); */
    }
}
