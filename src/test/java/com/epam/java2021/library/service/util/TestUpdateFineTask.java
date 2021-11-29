package com.epam.java2021.library.service.util;

import com.epam.java2021.library.dao.BookingDao;
import com.epam.java2021.library.dao.UserDao;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.entity.impl.Booking;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.epam.java2021.library.service.util.UpdateFineTask.INIT_PARAM_FINE_PER_DAY;
import static org.mockito.Mockito.*;


public class TestUpdateFineTask {
    private IDaoFactoryImpl daoFactory;
    private Booking booking;
    private User user;

    @Before
    public void mockDaoFactory() throws DaoException {
        long id = 1;
        daoFactory = mock(IDaoFactoryImpl.class);
        user = mock(User.class);
        booking = mock(Booking.class);
        Book book = mock(Book.class);
        BookingDao bookingDao = mock(BookingDao.class);
        UserDao userDao = mock(UserDao.class);
        List<User> users = new ArrayList<>();
        List<Booking> bookings = new ArrayList<>();
        List<Book> books = new ArrayList<>();

        books.add(book);
        bookings.add(booking);
        users.add(user);

        when(user.getId()).thenReturn(id);
        when(user.getFine()).thenReturn(0.0);
        when(book.getKeepPeriod()).thenReturn(1);
        when(booking.getBooks()).thenReturn(books);
        when(userDao.getAll()).thenReturn(users);
        when(bookingDao.findDeliveredByUserID(id)).thenReturn(bookings);

        when(daoFactory.getBookingDao()).thenReturn(bookingDao);
        when(daoFactory.getUserDao()).thenReturn(userDao);
    }

    @Test
    public void testInitWasNotCalled() {
        UpdateFineTask task = new UpdateFineTask(daoFactory);
        task.run();
        verifyZeroInteractions(daoFactory);
    }

    @Test
    public void testSetUpUserFine() throws ServiceException {
        UpdateFineTask task = new UpdateFineTask(daoFactory);
        ServletContext context = mock(ServletContext.class);
        when(context.getInitParameter(INIT_PARAM_FINE_PER_DAY)).thenReturn("1");
        task.init(context);

        Calendar twoDaysBefore = Calendar.getInstance();
        twoDaysBefore.add(Calendar.DATE, -2);
        when(booking.getModified()).thenReturn(twoDaysBefore);

        task.run();

        verify(user).setFine(1.0);
    }

    @Test
    public void testNotSetUpUserFine() throws ServiceException {
        UpdateFineTask task = new UpdateFineTask(daoFactory);
        ServletContext context = mock(ServletContext.class);
        when(context.getInitParameter(INIT_PARAM_FINE_PER_DAY)).thenReturn("1");
        task.init(context);

        when(booking.getModified()).thenReturn(Calendar.getInstance());
        task.run();

        verify(user, times(0)).setFine(1.0);
    }
}
