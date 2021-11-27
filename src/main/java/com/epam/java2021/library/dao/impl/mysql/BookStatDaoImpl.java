package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.impl.mysql.util.Transaction;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.entity.impl.BookStat;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookStatDaoImpl {
    private static final Logger logger = LogManager.getLogger(BookStatDaoImpl.class);
    private final DaoImpl<BookStat> dao;

    public BookStatDaoImpl(Connection conn) {
        dao = new DaoImpl<>(conn, logger);
    }

    public void create(BookStat bookStat) throws DaoException {
        final String query = "INSERT INTO book_stat (total, book_id) VALUES (?, ?)";

        dao.createBound(bookStat.getId(), bookStat, query, this::createFiller);
    }

    private int createFiller(BookStat bookStat, PreparedStatement ps) throws SQLException {
        int i = DaoImpl.START;
        ps.setLong(i++, bookStat.getTotal());
        return i;
    }

    public BookStat read(long id) throws DaoException {
        final String query = "SELECT * FROM book_stat WHERE book_id = ?";

        BookStat bookStat = dao.read(id, query, this::parse);
        return bookStat;
    }

    private BookStat parse(Connection c, ResultSet rs) throws SQLException {
        BookStat.Builder builder = new BookStat.Builder();
        builder.setId(rs.getInt("book_id"));
        builder.setTotal(rs.getInt("total"));
        builder.setInStock(rs.getInt("in_stock"));
        builder.setReserved(rs.getInt("reserved"));
        builder.setTimesWasBooked(rs.getInt("times_was_booked"));
        return builder.build();
    }

    public void update(BookStat entity) throws DaoException {
        final String query = "UPDATE book_stat SET total = ?, in_stock = ?, reserved = ?, times_was_booked = ?";

        dao.update(entity, query, this::updateFiller);
    }

    private int updateFiller(BookStat bookStat, PreparedStatement ps) throws SQLException {
        int i = DaoImpl.START;
        ps.setLong(i++, bookStat.getTotal());
        ps.setLong(i++, bookStat.getInStock());
        ps.setLong(i++, bookStat.getReserved());
        ps.setLong(i++, bookStat.getTimesWasBooked());
        return i;
    }
}
