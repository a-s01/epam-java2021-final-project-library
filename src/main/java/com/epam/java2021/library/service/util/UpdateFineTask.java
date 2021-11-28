package com.epam.java2021.library.service.util;

import com.epam.java2021.library.dao.BookingDao;
import com.epam.java2021.library.dao.UserDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.entity.impl.Booking;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;

import static com.epam.java2021.library.constant.Common.*;

public class UpdateFineTask extends AppPeriodicTask {
    private static final Logger logger = LogManager.getLogger(UpdateFineTask.class);
    private static final IDaoFactoryImpl daoFactory = DaoFactoryCreator.getDefaultFactory().getDefaultImpl();
    private static final String INIT_PARAM_FINE_PER_DAY = UpdateFineTask.class.getName() + ".finePerDay";
    private double finePerDay = -1;

    @Override
    public void run() {
        logger.info("update initiated");

        if (finePerDay == -1) {
            logger.fatal(TIMER_TASK_INIT_ERROR, INIT_PARAM_FINE_PER_DAY);
            return;
        }

        BookingDao bookingDao = daoFactory.getBookingDao();
        UserDao userDao = daoFactory.getUserDao();

        try {
            for (User user: userDao.getAll()) {
                checkUser(bookingDao, userDao, user);
            }
        } catch (DaoException e) {
            logger.error("Unable to get users list: {}", e.getMessage());
            return;
        }

        logger.info("update finished");
    }

    private void checkUser(BookingDao bookingDao, UserDao userDao, User user) {
        logger.trace("proceed user={}", user);
        double oldFine = user.getFine();
        Calendar now = Calendar.getInstance();

        List<Booking> bookings;
        try {
            bookings = bookingDao.findDeliveredByUserID(user.getId());
        } catch (DaoException e) {
            logger.error("Unable to get booking list for user: {}", e.getMessage());
            return;
        }

        for (Booking booking: bookings) {
            logger.trace("check booking={}", booking);

            Calendar lastModified = booking.getModified();
            long pastDays = ChronoUnit.DAYS.between(now.toInstant(), lastModified.toInstant());
            logger.trace("booking {}: {} days past", booking.getId(), pastDays);

            for (Book book : booking.getBooks()) {
                logger.trace("check book={}", book);
                long keepPeriod = booking.getLocated() == Booking.Place.USER ? book.getKeepPeriod() : 1;
                long diff = pastDays - keepPeriod;

                if (diff > 0) {
                    double fine = diff * finePerDay;
                    user.setFine(user.getFine() + fine);
                    logger.trace("keep period exceed, user fine increased on {}", fine);
                }
            }
        }

        if (user.getFine() != oldFine) {
            try {
                userDao.update(user);
            } catch (DaoException e) {
                logger.error(e.getMessage());
            } catch (ServiceException e) {
                logger.error("Error in your request: {}", e.getMessage());
            }
        }
    }

    @Override
    public void init(ServletContext context) throws ServiceException {
        logger.debug(START_MSG);

        String fine = context.getInitParameter(INIT_PARAM_FINE_PER_DAY);
        if (fine == null) {
            throw new ServiceException(INIT_PARAM_FINE_PER_DAY + " is not specified in web.xml");
        }

        try {
            finePerDay = Double.parseDouble(fine);
            if (finePerDay <= 0) {
                throw new NumberFormatException("it's not positive " + finePerDay);
            }
            logger.info("Fine per day initialized successfully: {}", finePerDay);
        } catch (NumberFormatException e) {
            throw new ServiceException(INIT_PARAM_FINE_PER_DAY + " should be valid positive double value: " + e.getMessage());
        }
        logger.debug(END_MSG);
    }
}
