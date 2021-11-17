package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.impl.Booking;

import java.util.Set;

public interface BookingDao extends AbstractDao<Booking> {
    void addBooks(Set<Long> bookIDs);
}
