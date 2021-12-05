package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.impl.Booking;
import com.epam.java2021.library.exception.DaoException;

import java.util.List;
/**
 * Functions specific to Booking class
 */
public interface BookingDao extends AbstractSuperDao<Booking> {
    List<Booking> findDeliveredByUserID(long id) throws DaoException;
}