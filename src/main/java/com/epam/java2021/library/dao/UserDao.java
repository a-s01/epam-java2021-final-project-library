package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.entityImpl.User;
import com.epam.java2021.library.exception.DaoException;

import java.util.List;

public interface UserDao extends EditableAbstractDao<User>, ComplexType<User>{
    User findByEmail(String email) throws DaoException;
    List<User> findByEmailPattern(String emailPattern) throws DaoException;
    List<User> getRecords(int page, int amount) throws DaoException;
}