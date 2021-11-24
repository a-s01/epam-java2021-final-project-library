package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.entity.impl.I18AuthorName;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// will be used only from AuthorDaoImpl
public class I18AuthorNameDaoImpl {
    private static final Logger logger = LogManager.getLogger(I18AuthorNameDaoImpl.class);
    private final Connection conn;

    public I18AuthorNameDaoImpl(Connection conn) {
        this.conn = conn;
    }

    public void create(long authorId, I18AuthorName name) throws DaoException {
        final String query = "INSERT INTO author_name_i18n (lang_id, name, author_id) VALUES (?, ?, ?)";

        DaoImpl<I18AuthorName> dao = new DaoImpl<>(conn, logger);
        dao.createBound(authorId, name, query, this::fillStatement);
    }

    private int fillStatement(I18AuthorName name, PreparedStatement ps) throws SQLException {
        int i = DaoImpl.START;
        ps.setLong(i++, name.getLangId());
        ps.setString(i, name.getName());
        return i;
    }

    public List<I18AuthorName> readByAuthorID(long id) throws DaoException {
        final String query = "SELECT * FROM author_name_i18n WHERE author_id = " + id;

        DaoImpl<I18AuthorName> dao = new DaoImpl<>(conn, logger);
        return dao.getRecords(query, this::parse);
    }

    private I18AuthorName parse(Connection c, ResultSet rs) throws SQLException {
        I18AuthorName.Builder builder = new I18AuthorName.Builder();
        builder.setId(rs.getInt("author_id"));
        builder.setLangId(rs.getInt("lang_id"));
        builder.setName(rs.getString("name"));
        return builder.build();
    }

    public void updateNamesForAuthor(long authorId, List<I18AuthorName> newList) throws DaoException {
        List<I18AuthorName> toDel = readByAuthorID(authorId);
        List<I18AuthorName> toAdd = new ArrayList<>();

        Collections.copy(toAdd, newList);

        toAdd.removeAll(toDel);
        toDel.removeAll(newList);

        for (I18AuthorName name: toDel) {
            delete(authorId, name);
        }

        for (I18AuthorName name: toAdd) {
            create(authorId, name);
        }
    }

    public void delete(long authorId, I18AuthorName name) throws DaoException {
        final String query = "DELETE FROM author_name_i18n WHERE lang_id = ? AND author_id = ?";

        DaoImpl<I18AuthorName> dao = new DaoImpl<>(conn, logger);
        dao.deleteBound(name.getLangId(), authorId, query);
    }
}
