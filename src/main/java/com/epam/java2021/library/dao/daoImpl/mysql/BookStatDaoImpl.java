package com.epam.java2021.library.dao.daoImpl.mysql;

import com.epam.java2021.library.dao.AbstractDao;
import com.epam.java2021.library.entity.entityImpl.BookStat;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookStatDaoImpl implements AbstractDao<BookStat> {
    private DaoImpl<BookStat> daoImpl;
    private static final Logger logger = LogManager.getLogger(BookStatDaoImpl.class);

    @Override
    public void create(BookStat bookStat) throws DaoException {
        final String query = "INSERT INTO book_stat VALUES (?, ?, DEFAULT, DEFAULT, DEFAULT)";
        daoImpl.create(bookStat, query, this::createFiller);
    }

    private void createFiller(BookStat bookStat, PreparedStatement ps) throws SQLException {
        int i = DaoImpl.START;
        ps.setLong(i++, bookStat.getId());
        ps.setLong(i++, bookStat.getTotal());
    }

    @Override
    public BookStat read(long id) throws DaoException {
        final String query = "SELECT * FROM book_stat WHERE book_id = ?";
        return daoImpl.read(id, query, this::parse);
    }

    private BookStat parse(ResultSet rs) throws SQLException {
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
        daoImpl.update(entity, query, this::updateFiller);
    }

    private void updateFiller(BookStat bookStat, PreparedStatement ps) throws SQLException {
        int i = DaoImpl.START;
        ps.setLong(i++, bookStat.getTotal());
        ps.setLong(i++, bookStat.getInStock());
        ps.setLong(i++, bookStat.getReserved());
        ps.setLong(i++, bookStat.getTimesWasBooked());
    }

    @Override
    public void delete(BookStat entity) throws DaoException {
        throw new UnsupportedOperationException("No need to implement this, it deletes automatically");
    }

    @Override
    public void setConnection(Connection conn) {
        daoImpl = new DaoImpl<BookStat>(conn, logger, "bookStat");
    }
}
