package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.BookingDao;
import com.epam.java2021.library.dao.impl.mysql.util.BaseDao;
import com.epam.java2021.library.dao.impl.mysql.util.SearchSortColumn;
import com.epam.java2021.library.dao.impl.mysql.util.Transaction;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.entity.impl.Booking;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.util.Disjoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Calendar;
import java.util.List;

import static com.epam.java2021.library.constant.Common.START_MSG;

public class BookingDaoImpl implements BookingDao {
    private static final Logger logger = LogManager.getLogger(BookingDaoImpl.class);
    private static final String BOOKING_COL = "state";
    private static final SearchSortColumn validColumns =
            new SearchSortColumn("email", "name", BOOKING_COL);
    private Connection conn;

    public BookingDaoImpl() {}
    public BookingDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void create(Booking booking) throws DaoException, ServiceException {
        logger.debug("create booking request init...");

        // user id we don't change
        final String query = "INSERT INTO booking VALUES(DEFAULT, ?, ?, ?, ?)";
        Transaction tr = new Transaction(conn);
        tr.transactionWrapper(c -> {
            BaseDao<Booking> dao = new BaseDao<>(c, logger);
            dao.create(booking, query, this::statementFiller);

            createBooksInBooking(new BaseDao<>(c, logger), booking.getId(), booking.getBooks());
        });
        logger.debug("create booking request finished");
    }

    @Override
    public Booking read(long id) throws DaoException, ServiceException {
        logger.debug("read booking request init...");

        final String query = "SELECT * FROM booking WHERE id = ?";
        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper(c -> {
            BaseDao<Booking> dao = new BaseDao<>(c, logger);
            Booking booking = dao.read(id, query, this::parse);

            BookDaoImpl bookDao = new BookDaoImpl(c);
            booking.setBooks(bookDao.getBooksInBooking(id));
            logger.debug("read booking request finished");
            return booking;
        });
    }

    @Override
    public void update(Booking booking) throws DaoException, ServiceException {
        logger.debug("update booking request init...");

        // user id we don't change
        final String query = "UPDATE booking SET user_id = ?, state = ?, located = ?, modified = ? WHERE id = ?";
        Transaction tr = new Transaction(conn);
        tr.transactionWrapper(c -> {
            BaseDao<Booking> dao = new BaseDao<>(c, logger);
            dao.update(booking, query, (b, ps) -> { int i = statementFiller(b, ps); ps.setLong(i++, b.getId()); return i; });

            BookDaoImpl bookDao = new BookDaoImpl(c);
            List<Book> oldList = bookDao.getBooksInBooking(booking.getId());
            updateBooksInBooking(c, booking.getId(), oldList, booking.getBooks());
        });
        logger.debug("update booking request finished");
    }

    private int statementFiller(Booking booking, PreparedStatement ps) throws SQLException {
        logger.debug("fill statement");

        int i = BaseDao.START;
        if (booking.getUser() == null) {
            throw new SQLException("user field cannot be null");
        }

        ps.setLong(i++, booking.getUser().getId());
        ps.setString(i++, booking.getState().name());
        ps.setString(i++, booking.getLocated().name());
        if (booking.getModified() == null) {
            throw new SQLException("modified field cannot be null");
        }
        ps.setTimestamp(i++, new Timestamp(booking.getModified().getTimeInMillis()));

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
            BaseDao<Booking> dao = new BaseDao<>(c, logger);
            List<Booking> bookings = dao.findById(id, query, this::parse);

            BookDaoImpl bookDao = new BookDaoImpl(c);
            for (Booking b: bookings) {
                List<Book> books = bookDao.getBooksInBooking(b.getId());
                b.setBooks(books);
            }
            return bookings;
        });
    }

    private Booking parse(Connection c, ResultSet rs) throws SQLException, DaoException {
        logger.debug("result set parsing init...");
        Booking.Builder builder = new Booking.Builder();

        builder.setId(rs.getInt("id"));
        builder.setState(Booking.State.valueOf(rs.getString(BOOKING_COL)));
        builder.setLocated(Booking.Place.valueOf(rs.getString("located")));

        Timestamp sqlTimestamp = rs.getTimestamp("modified");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sqlTimestamp);
        builder.setModified(cal);

        // get dependencies
        long userID = rs.getInt("user_id");
        UserDaoImpl userDao = new UserDaoImpl(c);
        User user = userDao.read(userID);
        builder.setUser(user);

        logger.debug("result set parsing finished");
        return builder.build();
    }

    @Override
    public List<Booking> findByPattern(String what, String searchBy, String sortBy, int num, int page) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        final String query = patternQuery(searchBy, sortBy, false, false);

        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper( c -> {
            BaseDao<Booking> dao = new BaseDao<>(c, logger);
            List<Booking> bookings = dao.findByPattern(what, num, page,query, this::parse);

            BookDaoImpl bookDao = new BookDaoImpl(c);
            for (Booking b: bookings) {
                List<Book> books = bookDao.getBooksInBooking(b.getId());
                b.setBooks(books);
            }

            return bookings;
        });
    }

    @Override
    public int findByPatternCount(String what, String searchBy) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        final String query = patternQuery(searchBy, null, true, false);
        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper(c -> {
            BaseDao<Booking> dao = new BaseDao<>(c, logger);
            return dao.count(what, query);
        });
    }

    @Override
    public List<Booking> findBy(String what, String searchBy) throws ServiceException, DaoException {
        logger.debug(START_MSG);

        final String query = patternQuery(searchBy, null, false, true);

        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper( c -> {
            BaseDao<Booking> dao = new BaseDao<>(c, logger);
            List<Booking> bookings = dao.findByString(what, query, this::parse);

            BookDaoImpl bookDao = new BookDaoImpl(c);
            for (Booking b: bookings) {
                List<Book> books = bookDao.getBooksInBooking(b.getId());
                b.setBooks(books);
            }

            return bookings;
        });
    }

    private String patternQuery(String searchBy, String sortBy, boolean count, boolean exactSearch) throws ServiceException {
        validColumns.check(searchBy, SearchSortColumn.SEARCH);

        final String searchCol = searchBy.equals(BOOKING_COL) ? "b." + searchBy : "u." + searchBy;
        final String what = count ? "COUNT(*)" : "*";
        final String operator = exactSearch ? " = ?" : " LIKE ?";

        String query = "SELECT " + what + " FROM booking AS b\n" +
                "  JOIN user AS u\n" +
                "    ON u.id = b.user_id\n" +
                " WHERE " + searchCol + operator;

        if (sortBy != null) {
            validColumns.check(sortBy, SearchSortColumn.SORT);

            final String orderCol = sortBy.equals(BOOKING_COL) ? "b." + sortBy : "u." + sortBy;
            query = query + " ORDER BY " + orderCol + " LIMIT ? OFFSET ?";
        }
        return query;
    }

    private void updateBooksInBooking(Connection c, long id, List<Book> oldList, List<Book> newList) throws DaoException {
        Disjoint<Book> disjoint = new Disjoint<>(oldList, newList);

        BaseDao<Book> dao = new BaseDao<>(c, logger);
        createBooksInBooking(dao, id, disjoint.getToAdd());
        deleteBooksInBooking(dao, id, disjoint.getToDelete());
    }

    private void deleteBooksInBooking(BaseDao<Book> dao, long id, List<Book> books) throws DaoException {
        // book_id, author_id
        final String delQuery = "DELETE FROM book_in_booking WHERE booking_id = ? AND book_id = ?";

        for (Book b: books) {
            dao.deleteBound(id, b.getId(), delQuery);
        }
    }

    private void createBooksInBooking(BaseDao<Book> dao, long id, List<Book> books) throws DaoException {
        // book_id, author_id
        final String addQuery = "INSERT INTO book_in_booking VALUES (?, ?)";

        for (Book b: books) {
            dao.update(b, addQuery, (book, ps) -> {
                int i = BaseDao.START;
                ps.setLong(i++, id);
                ps.setLong(i++, book.getId());
                return i;
            });
        }
    }
}
