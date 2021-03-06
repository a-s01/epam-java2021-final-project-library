package com.epam.java2021.library.dao;

import com.epam.java2021.library.entity.impl.Lang;
import com.epam.java2021.library.exception.DaoException;

import java.util.List;
/**
 * Functions specific to Lang class
 */
public interface LangDao {
    List<Lang> getAll() throws DaoException;
    Lang read(long id) throws DaoException;
    Lang read(String code) throws DaoException;
}
