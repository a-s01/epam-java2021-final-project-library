package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.impl.Booking;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;

import java.util.List;

public interface BookingDao extends SuperDao<Booking> {
    List<Booking> findDeliveredByUserID(long id) throws DaoException;
    List<Booking> findBy(String what, String searchBy) throws ServiceException, DaoException;
}