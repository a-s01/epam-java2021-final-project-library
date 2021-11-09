package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.entityImpl.User;
import java.util.List;

public interface UsersDao extends AbstractDao<User>{
    List<User> findByEmail(String email, boolean exactMatch) throws DaoException;
}
