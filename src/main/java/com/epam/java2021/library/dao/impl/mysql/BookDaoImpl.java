package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.AbstractDao;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.entity.impl.EditRecord;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Will be used only from BookSuperDao, so transaction isn't used here
 */
public class BookDaoImpl implements AbstractDao<Book> {
    private static final Logger logger = LogManager.getLogger(BookDaoImpl.class);
    private static final String AUTHOR_COL = "author";
    private final Connection conn;

    public BookDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void create(Book book) throws DaoException {
        final String query = "INSERT INTO book VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";

        DaoImpl<Book> daoImpl = new DaoImpl<>(conn, logger);
        daoImpl.create(book, query, this::fillStatement);
    }

    @Override
    public Book read(long id) throws DaoException {
        final String query = "SELECT * FROM book WHERE id = ?";

        DaoImpl<Book> daoImpl = new DaoImpl<>(conn, logger);
        return daoImpl.read(id, query, this::parse);
    }

    @Override
    public void update(Book book) throws DaoException {
        final String query = "UPDATE book SET title = ?, isbn = ?, year = ?, " +
                "lang_id = ?, keep_period = ?, last_edit_id = ?";

        DaoImpl<Book> dao = new DaoImpl<>(conn, logger);
        dao.update(book, query, this::fillStatement);
    }

    @Override
    public void delete(Book book) throws DaoException {
        final String query = "DELETE * FROM book WHERE id = ?";

        DaoImpl<Book> dao = new DaoImpl<>(conn, logger);
        dao.delete(book, query);
    }

    private Book parse(ResultSet rs) throws SQLException {
        Book.Builder builder = new Book.Builder();
        builder.setId(rs.getInt("id"));
        builder.setTitle(rs.getString("title"));
        builder.setIsbn(rs.getString("ISBN"));
        builder.setKeepPeriod(rs.getInt("keep_period"));
        builder.setCreated(rs.getDate("created"));
        builder.setYear(rs.getDate("year"));
        Book book = builder.build();

        // TODO deal with lang id
        long lastEditID = rs.getInt("last_edit_id");
        if (lastEditID != 0) {
            EditRecord dumb = new EditRecord.Builder().build();
            dumb.setId(lastEditID);
            book.setLastEdit(dumb);
        }
        return book;
    }

    private void fillStatement(Book book, PreparedStatement ps) throws SQLException {
        int i = DaoImpl.START;
        ps.setString(i++, book.getTitle());
        ps.setString(i++, book.getIsbn());
        ps.setInt(i++, book.getKeepPeriod());
        EditRecord lastEdit = book.getLastEdit();
        if (lastEdit != null) {
            ps.setLong(i++, lastEdit.getId());
        } else {
            ps.setNull(i++, Types.INTEGER);
        }
    }

    public List<Book> findByPattern(String pattern, String searchBy, String sortBy, int num, int page) throws DaoException {
        final String query = patternQuery(searchBy, sortBy, false);
        DaoImpl<Book> dao = new DaoImpl<>(conn, logger);
        return dao.findByPattern(pattern, num, page, query, this::parse);
    }

    private String patternQuery(String searchBy, String sortBy, boolean count) {
        final String orderCol = sortBy.equals(AUTHOR_COL) ? "a.name" : "b." + sortBy;
        final String searchCol = searchBy.equals(AUTHOR_COL) ? "a.name" : "b." + searchBy;
        final String what = count ? "COUNT(*)" : "*";

        String query = "SELECT " + what + "\n" +
                "  FROM book AS b\n" +
                "   JOIN book_author AS ba\n" +
                "      ON b.id=ba.book_id\n" +
                "      JOIN author AS a\n" +
                "        ON a.id = ba.author_id\n" +
                "     WHERE " + searchCol + " LIKE ?";
        if (count) {
            return query;
        }

        query = query + " ORDER BY " + orderCol + " LIMIT ? OFFSET ?";
        return query;
    }

    public int findByPatternCount(String pattern, String searchBy, String sortBy)
            throws DaoException {
        final String query = patternQuery(searchBy, sortBy, true);
        DaoImpl<Book> dao = new DaoImpl<>(conn, logger);
        return dao.count(pattern, query);
    }

    public List<Book> getBooksInBooking(long id) throws DaoException {
        final String query = "SELECT * FROM book_in_booking WHERE booking_id = ?";
        DaoImpl<Book> dao = new DaoImpl<>(conn, logger);

        List<Book> bookGerms = dao.findById(id, query, rs -> {
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
        final String delQuery = "DELETE FROM booking WHERE book_id = ?";

        List<Book> toDelete = getBooksInBooking(id);
        List<Book> toAdd = new ArrayList<>();
        Collections.copy(books, toAdd);
        toAdd.removeAll(toDelete);
        toDelete.removeAll(books);

        DaoImpl<Book> dao = new DaoImpl<>(conn, logger);
        createBooksInBooking(id, toAdd);

        for (Book b: toDelete) {
            dao.delete(b, delQuery);
        }
    }

    public void createBooksInBooking(long id, List<Book> books) throws DaoException {
        // book_id, author_id
        final String addQuery = "INSERT INTO book_in_booking VALUES (?, ?, ?)";

        DaoImpl<Book> dao = new DaoImpl<>(conn, logger);
        for (Book b: books) {
            dao.update(b, addQuery, (x, ps) -> {
                int i = DaoImpl.START;
                ps.setLong(i++, id);
                ps.setLong(i++, x.getId());
                ps.setInt(i++, x.getKeepPeriod());
            });
        }
    }
}
