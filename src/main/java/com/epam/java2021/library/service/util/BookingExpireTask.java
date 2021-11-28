package com.epam.java2021.library.service.util;

import com.epam.java2021.library.dao.BookingDao;
import com.epam.java2021.library.dao.factory.DaoFactoryCreator;
import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.entity.impl.Booking;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

import static com.epam.java2021.library.constant.Common.*;

public class BookingExpireTask extends AppPeriodicTask {
    public static final Logger logger = LogManager.getLogger(BookingExpireTask.class);
    public static final IDaoFactoryImpl daoFactory = DaoFactoryCreator.getDefaultFactory().getDefaultImpl();
    private static final String INIT_PARAM_PERIOD = UpdateFineTask.class.getName() + ".period";
    private int daysBeforeExpired = -1;

    @Override
    public void run() {
        logger.debug(START_MSG);
        if (daysBeforeExpired == -1) {
            logger.fatal(TIMER_TASK_INIT_ERROR, INIT_PARAM_PERIOD);
            return;
        }

        BookingDao dao = daoFactory.getBookingDao();
        try {
            for (Booking booking: dao.findBy("BOOKED", "state")) {
                logger.trace("check booking={}", booking);
                Calendar now = Calendar.getInstance();
                long pastDays = ChronoUnit.DAYS.between(now.toInstant(), booking.getModified().toInstant());

                if (pastDays >= daysBeforeExpired) {
                    booking.setState(Booking.State.CANCELED);
                    logger.info("booking (id {}) is expired", booking.getId());
                }
            }
            logger.info("All BOOKED bookings proceed");
        } catch (ServiceException | DaoException e) {
            logger.error("Unable to get list of BOOKED bookings: {}", e.getMessage());
        }

        logger.debug(END_MSG);
    }

    @Override
    public void init(ServletContext context) throws ServiceException {
        logger.debug(START_MSG);

        String periodStr = context.getInitParameter(INIT_PARAM_PERIOD);
        if (periodStr == null) {
            throw new ServiceException(INIT_PARAM_PERIOD + " is not specified in web.xml");
        }

        try {
            daysBeforeExpired = Integer.parseInt(periodStr);
            if (daysBeforeExpired <= 0) {
                throw new ServiceException("it's not positive " + daysBeforeExpired);
            }

        } catch (NumberFormatException e) {
            throw new ServiceException(INIT_PARAM_PERIOD + " should be valid positive integer value: " + e.getMessage());
        }

        logger.debug(END_MSG);
    }
}