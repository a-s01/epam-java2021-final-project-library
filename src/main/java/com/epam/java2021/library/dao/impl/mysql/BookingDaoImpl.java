package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.BookingDao;
import com.epam.java2021.library.dao.impl.mysql.util.SearchSortColumns;
import com.epam.java2021.library.dao.impl.mysql.util.Transaction;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.entity.impl.Booking;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookingDaoImpl implements BookingDao {
    private static final Logger logger = LogManager.getLogger(BookingDaoImpl.class);
    private static final String BOOKING_COL = "state";
    private static final SearchSortColumns searchSortColumns = new SearchSortColumns(logger, "email", "name", "state");
    private Connection conn;

    public BookingDaoImpl() {}
    public BookingDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void create(Booking booking) throws DaoException, ServiceException {
        logger.debug("create booking request init...");

        // user id we don't change
        final String query = "INSERT INTO booking VALUES(DEFAULT, ?, ?, ?, DEFAULT, DEFAULT)";
        Transaction tr = new Transaction(conn);
        tr.transactionWrapper(c -> {
            DaoImpl<Booking> dao = new DaoImpl<>(c, logger);
            dao.create(booking, query, this::statementFiller);
            BookDaoImpl bookDao = new BookDaoImpl(c);
            bookDao.createBooksInBooking(booking.getId(), booking.getBooks());
        });
        logger.debug("create booking request finished");
    }

    @Override
    public Booking read(long id) throws DaoException, ServiceException {
        logger.debug("read booking request init...");

        // user id we don't change
        final String query = "SELECT * FROM booking WHERE id = ?";
        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper(c -> {
            DaoImpl<Booking> dao = new DaoImpl<>(c, logger);
            Booking booking = dao.read(id, query, this::parse);

            BookSuperDao bookDao = new BookSuperDao(c);
            booking.setBooks(bookDao.getBooksInBooking(id));
            logger.debug("read booking request finished");
            return booking;
        });
    }

    @Override
    public void update(Booking booking) throws DaoException, ServiceException {
        logger.debug("update booking request init...");

        // user id we don't change
        final String query = "UPDATE booking SET user_id = ?, state = ?, located = ? WHERE id = ?";
        Transaction tr = new Transaction(conn);
        tr.transactionWrapper(c -> {
            DaoImpl<Booking> dao = new DaoImpl<>(c, logger);
            dao.update(booking, query, (b, ps) -> { int i = statementFiller(b, ps); ps.setLong(i, b.getId()); });
            BookDaoImpl bookDao = new BookDaoImpl(c);
            bookDao.updateBooksInBooking(booking.getId(), booking.getBooks());
        });
        logger.debug("update booking request finished");
    }

    private int statementFiller(Booking booking, PreparedStatement ps) throws SQLException {
        logger.debug("fill statement");

        int i = DaoImpl.START;
        ps.setLong(i++, booking.getUser().getId());
        ps.setString(i++, booking.getState().name());
        ps.setString(i++, booking.getLocated().name());

        logger.debug("finish filling statement");
        return i;
    }

    @Override
    public void delete(long id) throws DaoException, ServiceException {
        throw new UnsupportedOperationException("not yet supported");
    }

    @Override
    public List<Booking> findDeliveredByUserID(long id) throws DaoException {
        final String query = "SELECT * FROM booking WHERE state = 'DELIVERED' AND user_id = ?";

        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper( c -> {
            DaoImpl<Booking> dao = new DaoImpl<>(c, logger);
            List<Booking> bookings = dao.findById(id, query, this::parse);

            UserDaoImpl userDao = new UserDaoImpl(c);
            User u = userDao.read(id);

            BookSuperDao bookDao = new BookSuperDao(c);
            for (Booking b: bookings) {
                b.setUser(u);
                List<Book> books = bookDao.getBooksInBooking(b.getId());
                b.setBooks(books);
            }
            return bookings;
        });
    }

    private Booking parse(ResultSet rs) throws SQLException {
        logger.debug("result set parsing init...");
        Booking.Builder builder = new Booking.Builder();

        builder.setId(rs.getInt("id"));
        builder.setState(Booking.State.valueOf(rs.getString("state")));
        builder.setLocated(Booking.Place.valueOf(rs.getString("located")));
        builder.setCreated(rs.getDate("created"));

        User.Builder uBuilder = new User.Builder();
        uBuilder.setId(rs.getInt("user_id"));
        builder.setUser(uBuilder.build());

        logger.debug("result set pursing finished");
        return builder.build();
    }

    @Override
    public List<Booking> findByPattern(String what, String searchBy, String sortBy, int num, int page) throws ServiceException, DaoException {
        logger.debug("start");
        searchSortColumns.check(searchBy, "search");
        searchSortColumns.check(sortBy, "sort");

        final String query = patternQuery(searchBy, sortBy, false);

        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper( c -> {
            DaoImpl<Booking> dao = new DaoImpl<>(c, logger);
            List<Booking> bookings = dao.findByPattern(what, num, page,query, this::parse);

            UserDaoImpl userDao = new UserDaoImpl(c);
            BookSuperDao bookDao = new BookSuperDao(c);

            for (Booking b: bookings) {
                User u = userDao.read(b.getUser().getId());
                b.setUser(u);

                List<Book> books = bookDao.getBooksInBooking(b.getId());
                b.setBooks(books);
            }

            return bookings;
        });
    }

    @Override
    public int findByPatternCount(String what, String searchBy, String sortBy) throws ServiceException, DaoException {
        logger.debug("start");

        searchSortColumns.check(searchBy, "search");
        searchSortColumns.check(sortBy, "sort");

        final String query = patternQuery(searchBy, sortBy, true);
        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper(c -> {
            DaoImpl<Booking> dao = new DaoImpl<>(c, logger);
            return dao.count(what, query);
        });
    }

    private String patternQuery(String searchBy, String sortBy, boolean count) {
        final String orderCol = sortBy.equals(BOOKING_COL) ? "b." + sortBy : "u." + sortBy;
        final String searchCol = searchBy.equals(BOOKING_COL) ? "b" + sortBy : "u." + searchBy;
        final String what = count ? "COUNT(*)" : "*";

        String query = "SELECT " + what + " FROM booking AS b\n" +
                "  JOIN user AS u\n" +
                "    ON u.id = b.user_id\n" +
                " WHERE " + searchCol + " LIKE ?";
        if (count) {
            return query;
        }

        query = query + " ORDER BY " + orderCol + " LIMIT ? OFFSET ?";
        return query;
    }
}
