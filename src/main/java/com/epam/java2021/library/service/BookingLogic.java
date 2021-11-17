package com.epam.java2021.library.service;

import com.epam.java2021.library.dao.BookDao;
import com.epam.java2021.library.dao.SuperDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.entity.impl.BookStat;
import com.epam.java2021.library.entity.impl.Booking;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO make booking expire
public class BookingLogic {
    private static final Logger logger = LogManager.getLogger(BookingLogic.class);
    private static final IDaoFactoryImpl daoFactory = DaoFactoryCreator.getDefaultFactory().getDefaultImpl();

    public static Booking createBooking(User user) {
        logger.trace("createBooking request: user={}", user);
        Booking.Builder builder = new Booking.Builder();
        builder.setUser(user);
        return builder.build();
    }

    public static void addBook(Booking booking, long id) throws ServiceException, DaoException {
        logger.trace("addBook request: booking={}, id={}", booking, id);
        if (booking.getState() != Booking.State.NEW) {
            throw new ServiceException("Cannot add books to not NEW booking");
        }

        SuperDao<Book> bookDao = daoFactory.getBookDao();
        Book book = bookDao.read(id);
        BookStat bookStat = book.getBookStat();
        if ((bookStat.getInStock() - bookStat.getReserved()) > 0) {
            booking.getBooks().add(book);
        } else {
            throw new ServiceException("No free books for now, unable to reserve");
        }
    }

    public static void deleteBook(Booking booking, long id) throws ServiceException, DaoException {
        logger.trace("deleteBook request: booking={}, id={}", booking, id);

        if (booking == null) {
            throw new ServiceException("You should add some book first");
        }

        if (booking.getState() != Booking.State.NEW) {
            throw new ServiceException("Cannot delete books from not NEW booking");
        }

        SuperDao<Book> bookDao = daoFactory.getBookDao();
        Book book = bookDao.read(id);
        booking.getBooks().remove(book);
    }

    public static void cancel(Booking booking) throws ServiceException, DaoException {
        logger.trace("cancel request: booking={}", booking);

        Booking.State state = booking.getState();
        if (state != Booking.State.NEW && state != Booking.State.BOOKED) {
            throw new ServiceException("Cannot move booking to CANCELED state from any state except NEW and BOOKED");
        }

        if (state == Booking.State.NEW) {
            // nothing was added to DB yet, so just delete and forget
            booking.setBooks(null);
            return;
        }

        // so state was BOOKED and written to DB
        for (Book book: booking.getBooks()) {
            BookStat bookStat = book.getBookStat();
            bookStat.setReserved(bookStat.getReserved() - 1);
        }
        booking.setState(Booking.State.CANCELED);

        daoFactory.getBookingDao().update(booking);
    }

    public static void book(Booking booking) throws ServiceException, DaoException {
        logger.trace("book request: booking={}", booking);

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
    }

    public void deliver(Booking booking, boolean setUserLocation) throws ServiceException, DaoException {
        logger.trace("deliver request: booking={}", booking);

        if (booking.getState() != Booking.State.BOOKED) {
            throw new ServiceException("Cannot move booking to DELIVERED state from any state except of BOOKED");
        }

        booking.setState(Booking.State.DELIVERED);
        if (setUserLocation) {
            booking.setLocated(Booking.Place.USER);
        }

        for (Book book: booking.getBooks()) {
            BookStat stat = book.getBookStat();
            stat.setReserved(stat.getReserved() - 1);
            stat.setInStock(stat.getInStock() - 1);
        }

        daoFactory.getBookingDao().update(booking);
    }

    public void done(Booking booking) throws ServiceException, DaoException {
        logger.trace("done request: booking={}", booking);

        if (booking.getState() != Booking.State.DELIVERED) {
            throw new ServiceException("Cannot move booking to DONE state from any state except DELIVERED");
        }

        booking.setState(Booking.State.DONE);
        for (Book book: booking.getBooks()) {
            BookStat stat = book.getBookStat();
            stat.setInStock(stat.getInStock() + 1);
        }

        daoFactory.getBookingDao().update(booking);
    }
}
