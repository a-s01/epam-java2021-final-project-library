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
    static final String INIT_PARAM_FINE_PER_DAY = UpdateFineTask.class.getName() + ".finePerDay";
    private final IDaoFactoryImpl daoFactory;
    private volatile double finePerDay = -1;

    public UpdateFineTask() {
        daoFactory = DaoFactoryCreator.getDefaultFactory().getDefaultImpl();
    }

    public UpdateFineTask(IDaoFactoryImpl daoFactory) {
        this.daoFactory = daoFactory;
    }

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

        Calendar fineLastChecked = user.getFineLastChecked();
        for (Booking booking: bookings) {
            logger.trace("check booking={}", booking);

            Calendar lastModified =
                    booking.getModified().after(fineLastChecked) ? booking.getModified() : fineLastChecked;
            long pastDays = ChronoUnit.DAYS.between(lastModified.toInstant(), now.toInstant());
            logger.trace("booking {}: {} unchecked days past", booking.getId(), pastDays);

            for (Book book : booking.getBooks()) {
                logger.trace("check book={}", book);
                long keepPeriod = booking.getLocated() == Booking.Place.USER ? book.getKeepPeriod() : 1;
                long fineDays = pastDays - keepPeriod;

                if (fineDays > 0) {
                    logger.trace("fineDays={}", fineDays);
                    double fine = fineDays * finePerDay;
                    user.setFine(user.getFine() + fine);
                    logger.trace("keep period exceed, user fine increased on {}", fine);
                }
            }
        }

        if (user.getFine() != oldFine) {
            user.setModified(now);
            user.setFineLastChecked(now);
            try {
                userDao.update(user);
            } catch (DaoException e) {
                logger.error(e.getMessage());
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
            double finePerDayCandidate = Double.parseDouble(fine);
            if (finePerDayCandidate < 0) {
                throw new NumberFormatException("it's not positive " + finePerDayCandidate);
            }
            synchronized (this) {
                finePerDay = finePerDayCandidate;
            }
            logger.info("Fine per day initialized successfully: {}", finePerDayCandidate);
        } catch (NumberFormatException e) {
            throw new ServiceException(INIT_PARAM_FINE_PER_DAY + " should be valid positive double value: " + e.getMessage());
        }
        logger.debug(END_MSG);
    }
}
