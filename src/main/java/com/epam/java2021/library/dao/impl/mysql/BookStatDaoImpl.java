package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.AbstractDao;
import com.epam.java2021.library.dao.impl.mysql.util.Transaction;
import com.epam.java2021.library.entity.impl.BookStat;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookStatDaoImpl implements AbstractDao<BookStat> {
    private static final Logger logger = LogManager.getLogger(BookStatDaoImpl.class);
    private Connection conn;

    public BookStatDaoImpl() {}
    public BookStatDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void create(BookStat bookStat) throws DaoException {
        final String query = "INSERT INTO book_stat VALUES (?, ?, DEFAULT, DEFAULT, DEFAULT)";
        Transaction tr = new Transaction(conn);
        Connection c = tr.getConnection();

        DaoImpl<BookStat> dao = new DaoImpl<>(c, logger);
        dao.create(bookStat, query, this::createFiller);

        tr.close();
    }

    private int createFiller(BookStat bookStat, PreparedStatement ps) throws SQLException {
        int i = DaoImpl.START;
        ps.setLong(i++, bookStat.getId());
        ps.setLong(i++, bookStat.getTotal());
        return i;
    }

    @Override
    public BookStat read(long id) throws DaoException {
        final String query = "SELECT * FROM book_stat WHERE book_id = ?";
        Transaction tr = new Transaction(conn);
        Connection c = tr.getConnection();

        DaoImpl<BookStat> dao = new DaoImpl<>(c, logger);
        BookStat bookStat = dao.read(id, query, this::parse);

        tr.close();
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

    @Override
    public void update(BookStat entity) throws DaoException {
        final String query = "UPDATE book_stat SET total = ?, in_stock = ?, reserved = ?, times_was_booked = ?";
        Transaction tr = new Transaction(conn);
        Connection c = tr.getConnection();

        DaoImpl<BookStat> dao = new DaoImpl<>(c, logger);
        dao.update(entity, query, this::updateFiller);

        tr.close();
    }

    private int updateFiller(BookStat bookStat, PreparedStatement ps) throws SQLException {
        int i = DaoImpl.START;
        ps.setLong(i++, bookStat.getTotal());
        ps.setLong(i++, bookStat.getInStock());
        ps.setLong(i++, bookStat.getReserved());
        ps.setLong(i++, bookStat.getTimesWasBooked());
        return i;
    }

    @Override
    public void delete(long id) throws DaoException {
        throw new UnsupportedOperationException("No need to implement this, it deletes automatically on cascade");
    }
}
