package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.AuthorDao;
import com.epam.java2021.library.dao.impl.mysql.util.SearchSortColumn;
import com.epam.java2021.library.dao.impl.mysql.util.Transaction;
import com.epam.java2021.library.entity.impl.Author;
import com.epam.java2021.library.entity.impl.I18AuthorName;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorDaoImpl implements AuthorDao {
    private static final Logger logger = LogManager.getLogger(AuthorDaoImpl.class);
    private static final SearchSortColumn validColumns = new SearchSortColumn("name");
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
            List<Author> author = resolveDependencies(c, Collections.singletonList(dao.read(id, query, this::parse)));

            return author.get(0);
        });
    }

    @Override
    public Author read(String name) throws DaoException {
        final String query = "SELECT * FROM author WHERE name = ?";

        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper(c -> {
            DaoImpl<Author> dao = new DaoImpl<>(c, logger);
            Author author = dao.read(name, query, this::parse);
            if (author == null) {
                return null;
            }

            List<Author> list = resolveDependencies(c, Collections.singletonList(author));
            return list.get(0);
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

            return resolveDependencies(c, dao.findById(id, query, this::parse));
        });
    }

    private List<Author> resolveDependencies(Connection c, List<Author> authors) throws DaoException {
        if (authors == null) {
            return new ArrayList<>();
        }

        I18AuthorNameDaoImpl i18Dao = new I18AuthorNameDaoImpl(c);
        for (Author a: authors) {
            if (a == null) {
                continue;
            }
            a.setI18Names(i18Dao.readByAuthorID(a.getId()));
        }

        return authors;
    }

    @Override
    public List<Author> findByPattern(String what, String searchBy, String sortBy, int num, int page) throws ServiceException, DaoException {
        validColumns.check(searchBy, SearchSortColumn.SEARCH);
        validColumns.check(sortBy, SearchSortColumn.SORT);

        final String query =
                "SELECT * FROM author AS a" +
                "  JOIN author_name_i18n AS i18" +
                "    ON i18.author_id = a.id" +
                " WHERE i18.name LIKE ? " +
                " ORDER BY i18.name LIMIT ? OFFSET ?";
        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper(c -> {
            DaoImpl<Author> dao = new DaoImpl<>(c, logger);
            List<Author> list = dao.findByPattern(what, num, page, query, this::parse).stream()
                                    .distinct()
                                    .collect(Collectors.toList());
            return resolveDependencies(c, list);
        });
    }

    @Override
    public int findByPatternCount(String what, String searchBy) throws ServiceException, DaoException {
        validColumns.check(searchBy, SearchSortColumn.SEARCH);

        final String query =
                "SELECT * FROM author AS a" +
                        "  JOIN author_name_i18n AS i18" +
                        "    ON i18.author_id = a.id" +
                        " WHERE i18.name LIKE ? " +
                        " ORDER BY i18.name";
        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper(c -> {
            DaoImpl<Author> dao = new DaoImpl<>(c, logger);
            List<Author> list = dao.findByPattern(what, query, this::parse).stream()
                    .distinct()
                    .collect(Collectors.toList());
            return list.size();
        });
    }
}
