package com.epam.java2021.library.dao.daoImpl.mysql;

import com.epam.java2021.library.dao.AbstractDao;
import com.epam.java2021.library.dao.AuthorNamesDao;
import com.epam.java2021.library.entity.entityImpl.AuthorNames;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorNamesDaoImpl implements AuthorNamesDao {
    private static final Logger logger = LogManager.getLogger(AuthorNamesDaoImpl.class);
    private DaoImpl<AuthorNames> daoImpl;

    private void checkDaoImpl() throws ServiceException {
        if (daoImpl == null) {
            throw new ServiceException("You should call 'setConnection' function first");
        }
    }

    @Override
    public void create(AuthorNames entity) throws DaoException, ServiceException {
        checkDaoImpl();
    }

    @Override
    public AuthorNames read(long id) throws DaoException, ServiceException {
        checkDaoImpl();
        final String query = "SELECT * FROM author_name_i18n WHERE author_id = ?";
        return daoImpl.read(id, query, this::parse);
    }


    public AuthorNames readByLandId(long id) throws DaoException, ServiceException {
        checkDaoImpl();
        final String query = "SELECT * FROM author_name_i18n WHERE lang_id = ?";
        return daoImpl.read(id, query, this::parse);
    }

    private AuthorNames parse(ResultSet rs) throws SQLException {
        AuthorNames.Builder builder = new AuthorNames.Builder();
        builder.setId(rs.getInt("author_id"));
        builder.setLang_id(rs.getInt("lang_id"));
        builder.setName(rs.getString("name"));
        return builder.build();
    }

    @Override
    public void update(AuthorNames entity) throws DaoException, ServiceException {
        checkDaoImpl();

    }

    @Override
    public void delete(AuthorNames entity) throws DaoException, ServiceException {
        checkDaoImpl();

    }

    @Override
    public void setConnection(Connection conn) {
        daoImpl = new DaoImpl<AuthorNames>(conn, logger, "author_names");
    }
}
