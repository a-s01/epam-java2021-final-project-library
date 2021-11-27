package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.entity.impl.Lang;
import com.epam.java2021.library.service.util.Disjoint;
import com.epam.java2021.library.entity.impl.I18AuthorName;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.epam.java2021.library.constant.Common.START_MSG;

// will be used only from AuthorDaoImpl
public class I18AuthorNameDaoImpl {
    private static final Logger logger = LogManager.getLogger(I18AuthorNameDaoImpl.class);
    private final DaoImpl<I18AuthorName> dao;
    private final LangDaoImpl langDao;

    public I18AuthorNameDaoImpl(Connection conn) {
        this.dao = new DaoImpl<>(conn, logger);
        this.langDao = new LangDaoImpl(conn);
    }

    public void create(long authorId, I18AuthorName name) throws DaoException {
        logger.debug(START_MSG);
        logger.trace("authorID={}, i18name={}", authorId, name);
        final String query = "INSERT INTO author_name_i18n (lang_id, name, author_id) VALUES (?, ?, ?)";

        dao.createBound(authorId, name, query, this::fillStatement);
    }

    private int fillStatement(I18AuthorName name, PreparedStatement ps) throws SQLException {
        int i = DaoImpl.START;
        ps.setLong(i++, name.getLang().getId());
        ps.setString(i++, name.getName());
        return i;
    }

    public List<I18AuthorName> readByAuthorID(long id) throws DaoException {
        logger.debug(START_MSG);
        logger.trace("id={}", id);
        final String query = "SELECT * FROM author_name_i18n WHERE author_id = " + id;

        return dao.getRecords(query, this::parse);
    }

    private I18AuthorName parse(Connection c, ResultSet rs) throws SQLException, DaoException {
        I18AuthorName.Builder builder = new I18AuthorName.Builder();
        builder.setId(rs.getInt("author_id"));
        long langID = rs.getInt("lang_id");

        builder.setLang(langDao.read(langID));
        builder.setName(rs.getString("name"));
        return builder.build();
    }

    public void updateNamesForAuthor(long authorId, List<I18AuthorName> newList) throws DaoException {
        logger.debug(START_MSG);
        logger.trace("authorId={}, i18names={}", authorId, newList);
        Disjoint<I18AuthorName> disjoint = new Disjoint<>(readByAuthorID(authorId), newList);

        for (I18AuthorName name: disjoint.getToDelete()) {
            delete(authorId, name);
        }

        for (I18AuthorName name: disjoint.getToAdd()) {
            create(authorId, name);
        }
    }

    public void delete(long authorId, I18AuthorName name) throws DaoException {
        logger.debug(START_MSG);
        logger.trace("authorId={}, i18name={}", authorId, name);
        final String query = "DELETE FROM author_name_i18n WHERE lang_id = ? AND author_id = ?";

        dao.deleteBound(name.getLang().getId(), authorId, query);
    }
}
