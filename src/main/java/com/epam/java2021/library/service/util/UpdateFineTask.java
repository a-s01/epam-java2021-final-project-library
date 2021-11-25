package com.epam.java2021.library.service.util;

import com.epam.java2021.library.constant.Common;
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

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.TimerTask;

public class UpdateFineTask extends TimerTask {
    private static final Logger logger = LogManager.getLogger(UpdateFineTask.class);

    @Override
    public void run() {
        logger.info("update initiated");
        IDaoFactoryImpl daoFactory = DaoFactoryCreator.getDefaultFactory().getDefaultImpl();

        BookingDao bookingDao = daoFactory.getBookingDao();
        UserDao userDao = daoFactory.getUserDao();
        try {
            for (User user: userDao.getAll()) {
                double oldFine = user.getFine();
                Calendar now = Calendar.getInstance();

                for (Booking booking: bookingDao.findDeliveredByUserID(user.getId())) {
                    Calendar lastModified = booking.getModified();
                    long pastDays = ChronoUnit.DAYS.between(now.toInstant(), lastModified.toInstant());

                    for (Book book : booking.getBooks()) {
                        long keepPeriod = booking.getLocated() == Booking.Place.USER ? book.getKeepPeriod() : 1;
                        long diff = pastDays - keepPeriod;

                        if (diff > 0) {
                            user.setFine(user.getFine() + (diff * Common.FINE_PER_DAY));
                        }
                    }
                }

                if (user.getFine() != oldFine) {
                    userDao.update(user);
                }
            }
        } catch (ServiceException e) {
            logger.error("Error in your request: {}", e.getMessage());
        } catch (DaoException e) {
            logger.error(e.getMessage());
        }
        logger.info("update finished");
    }
}
