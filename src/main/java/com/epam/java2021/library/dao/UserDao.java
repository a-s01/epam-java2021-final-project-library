package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;

import java.util.List;

public interface UserDao extends SuperDao<User>{
    User findByEmail(String email) throws DaoException;
    List<User> getAll() throws DaoException;
}