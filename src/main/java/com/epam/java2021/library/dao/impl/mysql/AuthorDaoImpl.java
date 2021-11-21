package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.AuthorDao;
import com.epam.java2021.library.dao.impl.mysql.util.Transaction;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.entity.impl.Author;
import com.epam.java2021.library.entity.impl.EditRecord;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// TODO add i18n
public class AuthorDaoImpl implements AuthorDao {
    private static final Logger logger = LogManager.getLogger(AuthorDaoImpl.class);
    private Connection conn;

    public AuthorDaoImpl() {}
    public AuthorDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void create(Author author) throws DaoException {
        final String query = "INSERT INTO author VALUES (DEFAULT, ?, DEFAULT, DEFAULT)";

        Transaction transaction = new Transaction(conn);
        Connection c = transaction.getConnection();

        DaoImpl<Author> dao = new DaoImpl<>(c, logger);
        dao.create(author, query, this::statementFiller);

        transaction.close();
    }

    private void statementFiller(Author author, PreparedStatement ps) throws SQLException {
        ps.setString(DaoImpl.START, author.getName());
    }

    @Override
    public Author read(long id) throws DaoException, ServiceException {
        final String query = "SELECT * FROM author WHERE id = ?";
        throw new UnsupportedOperationException("not supported");
        //return daoImpl.read(id, query, this::parse);
    }

    private Author parse(ResultSet rs) throws SQLException {
        logger.trace("parse author...");
        Author.Builder builder = new Author.Builder();
        builder.setId(rs.getInt("id"));
        builder.setName(rs.getString("name"));
        builder.setCreated(rs.getDate("created"));
        Author author = builder.build();

        long lastEditId = rs.getInt("last_edit_id");
        if (lastEditId != 0) {
            EditRecord dumb = new EditRecord.Builder().build();
            dumb.setId(lastEditId);
            author.setLastEdit(dumb);
        }
        logger.trace("parse author result: {}", author);
        return author;
    }

    @Override
    public void update(Author entity) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void delete(Author entity) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public List<Author> findByName(String pattern, int num, int page) throws DaoException {
        final String query = "SELECT * FROM author WHERE name LIKE ? ORDER by name LIMIT ? OFFSET ?";

        Transaction tr = new Transaction(conn);
        Connection c = tr.getConnection();

        DaoImpl<Author> dao = new DaoImpl<>(c, logger);
        List<Author> authors = dao.findByPattern(pattern, num, page, query, this::parse);

        tr.close();
        return authors;
    }

    @Override
    public List<Author> findByBookID(long id) throws DaoException {
        final String query = "SELECT a.id, a.name, a.created, a.last_edit_id FROM author AS a\n" +
                "  JOIN book_author AS ba\n" +
                "    ON ba.author_id = a.id\n" +
                "  JOIN book AS b\n" +
                "    ON b.id = ba.book_id\n" +
                " WHERE b.id = ?;  ";

        Transaction tr = new Transaction(conn);
        Connection c = tr.getConnection();

        DaoImpl<Author> dao = new DaoImpl<>(c, logger);
        List<Author> authors = dao.findById(id, query, this::parse);

        tr.close();
        return authors;
    }

    public void setLastEdit(Author author, EditRecord lastEdit) throws DaoException, ServiceException {
        final String query = "UPDATE author SET last_edit_id = ? WHERE id = ?";
        throw new UnsupportedOperationException("not supported");
    }
}
