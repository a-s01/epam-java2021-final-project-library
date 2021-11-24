package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.AbstractDao;
import com.epam.java2021.library.entity.impl.Author;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.service.util.Disjoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Will be used only from BookSuperDao, so transaction isn't used here
 */
public class BookDaoImpl implements AbstractDao<Book> {
    private static final Logger logger = LogManager.getLogger(BookDaoImpl.class);
    private static final String AUTHOR_COL = "author";
    private final DaoImpl<Book> dao;

    public BookDaoImpl(Connection conn) {
        dao = new DaoImpl<>(conn, logger);
    }

    @Override
    public void create(Book book) throws DaoException {
        final String query = "INSERT INTO book VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";

        dao.create(book, query, this::fillStatement);
        for (Author author: book.getAuthors()) {
            createBound(book, author);
        }
    }

    @Override
    public Book read(long id) throws DaoException {
        final String query = "SELECT * FROM book WHERE id = ?";

        return dao.read(id, query, this::parse);
    }

    @Override
    public void update(Book book) throws DaoException {
        final String query = "UPDATE book SET title = ?, isbn = ?, year = ?, " +
                "lang_code = ?, keep_period = ?, modified = ? WHERE id = ?";

        dao.update(book, query,
                (b, ps) -> {
                    int last = fillStatement(b, ps);
                    ps.setLong(last++, b.getId());
                    return last;
                }
        );
    }

    @Override
    public void delete(long id) throws DaoException {
        final String query = "DELETE FROM book WHERE id = ?";

        dao.delete(id, query);
    }

    private Book parse(Connection c, ResultSet rs) throws SQLException {
        Book.Builder builder = new Book.Builder();
        builder.setId(rs.getInt("id"));
        builder.setTitle(rs.getString("title"));
        builder.setIsbn(rs.getString("ISBN"));
        builder.setKeepPeriod(rs.getInt("keep_period"));

        Timestamp sqlTime = rs.getTimestamp("modified");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sqlTime);
        builder.setModified(cal);

        builder.setYear(rs.getDate("year"));
        builder.setLangCode(rs.getString("lang_code"));

        return builder.build();
    }

    private int fillStatement(Book book, PreparedStatement ps) throws SQLException {
        int i = DaoImpl.START;
        ps.setString(i++, book.getTitle());
        ps.setString(i++, book.getIsbn());
        ps.setInt(i++, book.getYear());
        ps.setString(i++, book.getLangCode());
        ps.setInt(i++, book.getKeepPeriod());
        ps.setTimestamp(i++, new Timestamp(book.getModified().getTimeInMillis()));

        return i;
    }

    public List<Book> findByPattern(String pattern, String searchBy, String sortBy, int num, int page) throws DaoException {
        final String query = patternQuery(searchBy, sortBy, false, false);
        return dao.findByPattern(pattern, num, page, query, this::parse);
    }

    public List<Book> findBy(String pattern, String searchBy) throws DaoException {
        final String query = patternQuery(searchBy, null, false, true);
        return dao.findByPattern(pattern, query, this::parse);
    }

    private String patternQuery(String searchBy, String sortBy, boolean count, boolean exactSearch) {

        final String searchCol = searchBy.equals(AUTHOR_COL) ? "a.name" : "b." + searchBy;
        final String what = count ? "COUNT(*)" : "*";
        final String operator = exactSearch ? " = ?" : " LIKE ?";

        String query = "SELECT " + what + "\n" +
                "  FROM book AS b\n" +
                "   JOIN book_author AS ba\n" +
                "      ON b.id=ba.book_id\n" +
                "      JOIN author AS a\n" +
                "        ON a.id = ba.author_id\n" +
                "     WHERE " + searchCol + operator;

        if (sortBy != null) {
            final String orderCol = sortBy.equals(AUTHOR_COL) ? "a.name" : "b." + sortBy;
            query = query + " ORDER BY " + orderCol + " LIMIT ? OFFSET ?";
        }

        return query;
    }

    public int findByPatternCount(String pattern, String searchBy, String sortBy)
            throws DaoException {
        final String query = patternQuery(searchBy, sortBy, true, false);
        return dao.count(pattern, query);
    }

    public List<Book> getBooksInBooking(long id) throws DaoException {
        final String query = "SELECT * FROM book_in_booking WHERE booking_id = ?";

        List<Book> bookGerms = dao.findById(id, query, (c, rs) -> {
            Book.Builder builder = new Book.Builder();
            builder.setId(rs.getInt("book_id"));
            return builder.build();
        });

        List<Book> books = new ArrayList<>();
        for (Book g: bookGerms) {
            Book book = read(g.getId());
            books.add(book);
        }
        return books;
    }

    public void updateBooksInBooking(long id, List<Book> books) throws DaoException {
        // book_id, author_id
        final String delQuery = "DELETE FROM booking WHERE booking_id = ? AND book_id = ?";

        Disjoint<Book> disjoint = new Disjoint<>(getBooksInBooking(id), books);

        createBooksInBooking(id, disjoint.getToAdd());

        for (Book b: disjoint.getToDelete()) {
            dao.deleteBound(id, b.getId(), delQuery);
        }
    }

    public void createBooksInBooking(long id, List<Book> books) throws DaoException {
        // book_id, author_id
        final String addQuery = "INSERT INTO book_in_booking VALUES (?, ?, ?)";

        for (Book b: books) {
            dao.update(b, addQuery, (x, ps) -> {
                int i = DaoImpl.START;
                ps.setLong(i++, id);
                ps.setLong(i++, x.getId());
                ps.setInt(i++, x.getKeepPeriod());
                return i;
            });
        }
    }

    public void deleteBound(Book book, Author author) throws DaoException {
        final String boundQuery = "DELETE FROM book_author WHERE book_id = ? and author_id = ?";

        dao.updateBound(book.getId(), author.getId(), boundQuery);
    }

    public void createBound(Book book, Author author) throws DaoException {
        final String boundQuery = "INSERT INTO book_author VALUES (?, ?)";

        dao.updateBound(book.getId(), author.getId(), boundQuery);
    }
}
