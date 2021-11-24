package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.AuthorDao;
import com.epam.java2021.library.dao.impl.mysql.util.Transaction;
import com.epam.java2021.library.entity.impl.Author;
import com.epam.java2021.library.entity.impl.I18AuthorName;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Calendar;
import java.util.List;

public class AuthorDaoImpl implements AuthorDao {
    private static final Logger logger = LogManager.getLogger(AuthorDaoImpl.class);
    private Connection conn;

    public AuthorDaoImpl() {}
    public AuthorDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void create(Author author) throws DaoException {
        final String query = "INSERT INTO author VALUES (DEFAULT, ?, ?)";

        Transaction tr = new Transaction(conn);
        tr.transactionWrapper( c -> {
            DaoImpl<Author> dao = new DaoImpl<>(c, logger);
            dao.create(author, query, this::statementFiller);

            I18AuthorNameDaoImpl i18Dao = new I18AuthorNameDaoImpl(c);
            for (I18AuthorName name: author.getI18Names()) {
                i18Dao.create(author.getId(), name);
            }
        });
    }

    private int statementFiller(Author author, PreparedStatement ps) throws SQLException {
        int i = DaoImpl.START;
        ps.setString(i++, author.getName());
        ps.setTimestamp(i++, new Timestamp(author.getModified().getTimeInMillis()));
        return i;
    }

    @Override
    public Author read(long id) throws DaoException, ServiceException {
        final String query = "SELECT * FROM author WHERE id = ?";

        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper(c -> {
            DaoImpl<Author> dao = new DaoImpl<>(c, logger);
            Author author = dao.read(id, query, this::parse);

            I18AuthorNameDaoImpl i18Dao = new I18AuthorNameDaoImpl(c);
            author.setI18Names(i18Dao.readByAuthorID(id));

            return author;
        });
    }

    private Author parse(Connection c, ResultSet rs) throws SQLException {
        logger.trace("parse author...");
        Author.Builder builder = new Author.Builder();
        builder.setId(rs.getInt("id"));
        builder.setName(rs.getString("name"));

        Timestamp sqlTime = rs.getTimestamp("modified");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sqlTime);
        builder.setModified(cal);

        Author author = builder.build();

        logger.trace("parse author result: {}", author);
        return author;
    }

    @Override
    public void update(Author author) throws DaoException {
        final String query = "UPDATE author SET name = ? WHERE id = ?";

        Transaction tr = new Transaction(conn);
        tr.transactionWrapper( c -> {
            DaoImpl<Author> dao = new DaoImpl<>(c, logger);
            dao.update(author, query, this::statementFiller);

            I18AuthorNameDaoImpl i18Dao = new I18AuthorNameDaoImpl(c);
            i18Dao.updateNamesForAuthor(author.getId(), author.getI18Names());
        });
    }

    @Override
    public void delete(long id) throws DaoException {
        final String query = "DELETE FROM author WHERE id = ?";

        Transaction tr = new Transaction(conn);
        tr.transactionWrapper(c -> {
            DaoImpl<Author> dao = new DaoImpl<>(c, logger);
            dao.delete(id, query); // i18n on delete cascade
        });
    }

    @Override
    public List<Author> findByBookID(long id) throws DaoException {
        final String query = "SELECT a.id, a.name, a.modified FROM author AS a\n" +
                "  JOIN book_author AS ba\n" +
                "    ON ba.author_id = a.id\n" +
                "  JOIN book AS b\n" +
                "    ON b.id = ba.book_id\n" +
                " WHERE b.id = ?;  ";

        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper(c -> {
            DaoImpl<Author> dao = new DaoImpl<>(c, logger);
            List<Author> authors = dao.findById(id, query, this::parse);

            I18AuthorNameDaoImpl i18Dao = new I18AuthorNameDaoImpl(c);
            for (Author a: authors) {
                a.setI18Names(i18Dao.readByAuthorID(a.getId()));
            }

            return authors;
        });
    }
}
