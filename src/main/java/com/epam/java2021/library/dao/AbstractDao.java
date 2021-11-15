package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.exception.DaoException;

import java.sql.Connection;
import java.util.List;

public interface AbstractDao<E extends Entity> {
    void create(E entity) throws DaoException;
    E read(long id) throws DaoException;
    void update(E entity) throws DaoException;
    void delete(E entity) throws DaoException;
    List<E> getRecords(int page, int amount) throws DaoException;
    void setConnection(Connection conn);
}