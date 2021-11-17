package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.AbstractDao;
import com.epam.java2021.library.entity.impl.Language;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LangDaoImpl {
    /*private static final Logger logger = LogManager.getLogger(LangDaoImpl.class);
    private DaoImpl<Language> daoImpl;

    @Override
    public void create(Language entity) throws DaoException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public Language read(long id) throws DaoException {
        final String query = "SELECT * FROM lang WHERE id = ?";
        return daoImpl.read(id, query, this::parse);
    }

    private Language parse(ResultSet rs) throws SQLException {
        Language.Builder builder = new Language.Builder();

        builder.setId(rs.getInt("id"));
        builder.setCode(rs.getString("code"));

        return builder.build();
    }

    @Override
    public void update(Language entity) throws DaoException {
        throw new UnsupportedOperationException("not supported");

    }

    @Override
    public void delete(Language entity) throws DaoException {
        throw new UnsupportedOperationException("Deletion of language isn't supported");
    }

    @Override
    public void setConnection(Connection conn) {
        daoImpl = new DaoImpl<>(conn, logger, "language");
    }

     */
}
