package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.impl.Booking;
import com.epam.java2021.library.exception.DaoException;

import java.util.List;

public interface BookingDao extends SuperDao<Booking> {
    List<Booking> findDeliveredByUserID(long id) throws DaoException;
}