package com.epam.java2021.library.dao.daoImpl.mysql;

import com.epam.java2021.library.dao.AuthorDao;
import com.epam.java2021.library.dao.ComplexType;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.DependencyHolder;
import com.epam.java2021.library.entity.entityImpl.Author;
import com.epam.java2021.library.entity.entityImpl.EditRecord;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AuthorDaoImpl implements AuthorDao, ComplexType<Author> {
    private static final Logger logger = LogManager.getLogger(AuthorDaoImpl.class);
    private DaoImpl<Author> daoImpl;
    private DependencyHolder<Author> holder = new DependencyHolder<>();

    private void checkDaoImpl() throws ServiceException {
        if (daoImpl == null) {
            throw new ServiceException("You should call 'setConnection' function first");
        }
    }

    @Override
    public void create(Author author) throws DaoException, ServiceException {
        checkDaoImpl();
        final String query = "INSERT INTO author VALUES (DEFAULT, ?, DEFAULT, DEFAULT)";
        daoImpl.create(author, query, this::statementFiller);
    }

    private void statementFiller(Author author, PreparedStatement ps) throws SQLException {
        ps.setString(DaoImpl.START, author.getName());
    }

    @Override
    public Author read(long id) throws DaoException, ServiceException {
        checkDaoImpl();
        final String query = "SELECT * FROM author WHERE id = ?";
        return daoImpl.read(id, query, this::parse);
    }

    private Author parse(ResultSet rs) throws SQLException {
        logger.trace("parse author...");
        Author.Builder builder = new Author.Builder();
        builder.setId(rs.getInt("id"));
        builder.setName(rs.getString("name"));
        builder.setCreated(rs.getDate("created"));
        Author author = builder.build();
        holder.set(author, "lastEditID", rs.getInt("last_edit_id"));
        logger.trace("parse author result: {}", author);
        return author;
    }

    @Override
    public void update(Author entity) throws DaoException, ServiceException {
        checkDaoImpl();
    }

    @Override
    public void delete(Author entity) throws DaoException, ServiceException {
        checkDaoImpl();
    }

    @Override
    public void setConnection(Connection conn) {
        daoImpl = new DaoImpl<>(conn, logger, "author");
    }

    @Override
    public List<Author> findByName(String pattern) throws DaoException, ServiceException {
        checkDaoImpl();
        final String query = "SELECT * FROM author WHERE name LIKE ? ORDER by name";
        return daoImpl.findByPattern(pattern, query, this::parse);
    }

    @Override
    public List<Author> findByBookID(long id) throws DaoException, ServiceException {
        checkDaoImpl();
        final String query = "SELECT a.id, a.name, a.created, a.last_edit_id FROM author AS a\n" +
                "  JOIN book_author AS ba\n" +
                "    ON ba.author_id = a.id\n" +
                "\tJOIN book AS b\n" +
                "      ON b.id = ba.book_id\n" +
                "   WHERE b.id = ?;  ";
        return daoImpl.findById(id, query, this::parse);
    }

    @Override
    public void setLastEdit(Author author, EditRecord lastEdit) throws DaoException, ServiceException {
        checkDaoImpl();
        final String query = "UPDATE author SET last_edit_id = ? WHERE id = ?";
        author.setLastEdit(lastEdit);
        daoImpl.updateLongField(lastEdit.getId(), author, query);
    }

    @Override
    public DependencyHolder<Author> getDependencies() {
        return holder;
    }
}
