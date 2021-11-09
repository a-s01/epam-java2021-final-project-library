package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.entityImpl.Booking;
import com.epam.java2021.library.entity.entityImpl.User;

import java.util.List;

public interface BookingDao extends AbstractDao<Booking>{
    List<Booking> findByUser(User user) throws DaoException;
}
