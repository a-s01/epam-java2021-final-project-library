package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.UserDao;
import com.epam.java2021.library.dao.impl.mysql.util.Transaction;
import com.epam.java2021.library.entity.impl.User;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDaoImpl implements UserDao {
    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);
    private Connection conn;

    public UserDaoImpl() {}
    public UserDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public User findByEmail(String email) throws DaoException {
        final String query = "SELECT * FROM user WHERE email = ?";
        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper( c -> {
            DaoImpl<User> dao = new DaoImpl<>(c, logger);
            return dao.findByUniqueString(email, query, this::parse);
        });
    }

    public static class SearchSortColumns {
        private SearchSortColumns() {}
        private static final Set<String> COLUMNS = new HashSet<>();
        static {
            COLUMNS.add("email");
            COLUMNS.add("name");
            COLUMNS.add("role");
            COLUMNS.add("state");
        }

        public static void check(String s, String action) throws ServiceException {
            if (!COLUMNS.contains(s)) {
                logger.error("{} forbidden for column {}", action, s);
                throw new ServiceException(action + " forbidden for column " + s);
            }
        }
    }

    @Override
    public void create(User user) throws DaoException {
        final String query = "INSERT INTO user VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Transaction tr = new Transaction(conn);
        tr.transactionWrapper( c -> {
            DaoImpl<User> dao = new DaoImpl<>(c, logger);
            dao.create(user, query, this::fillStatement);
        });
    }

    private int fillStatement(User user, PreparedStatement ps) throws SQLException {
        int i = DaoImpl.START;
        ps.setString(i++, user.getEmail());
        ps.setString(i++, user.getPassword());
        ps.setString(i++, user.getSalt());
        ps.setInt(i++, user.getRole().ordinal());
        ps.setInt(i++, user.getState().ordinal());
        ps.setDouble(i++, user.getFine());
        ps.setString(i++, user.getName());
        ps.setLong(i++, user.getPreferredLang().getId());
        ps.setDate(i++, new Date(user.getModified().getTimeInMillis()));
        return i;
    }

    @Override
    public User read(long id) throws DaoException {
        final String query = "SELECT * FROM user WHERE id = ?";
        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper( c -> {
            DaoImpl<User> dao = new DaoImpl<>(c, logger);
            return dao.read(id, query, this::parse);
        });
    }

    private User parse(Connection c, ResultSet rs) throws SQLException {
        User.Builder builder = new User.Builder();
        builder.setId(rs.getInt("id"));
        builder.setEmail(rs.getString("email"));
        builder.setName(rs.getString("name"));
        builder.setPassword(rs.getString("password"));
        builder.setSalt(rs.getString("salt"));
        String dbRole = rs.getString("role");
        builder.setRole(User.Role.valueOf(dbRole));
        String dbState = rs.getString("state");
        builder.setState(User.State.valueOf(dbState));
        builder.setFine(rs.getDouble("fine"));

        Date sqlDate = rs.getDate("modified");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sqlDate);
        builder.setModified(cal);

        // get dependencies
        long langID = rs.getInt("preferred_lang_id");
        LangDaoImpl langDao = new LangDaoImpl(c);
        builder.setPreferredLang(langDao.read(langID));

        return builder.build();
    }

    @Override
    public void update(User user) throws DaoException {
        final String query = "UPDATE user SET email = ?, password = ?, salt = ?, role = ?, state = ?, " +
                "fine = ?, name = ?, preferred_lang_id = ?, modified = ? WHERE id = ?";
        Transaction tr = new Transaction(conn);
        tr.transactionWrapper(c -> {
            DaoImpl<User> dao = new DaoImpl<>(c, logger);
            dao.update(user, query,
                    (entity, ps) -> {
                        int nextIndex = fillStatement(entity, ps);
                        ps.setLong(nextIndex, entity.getId());
                    }
            );
        });
    }

    @Override
    public void delete(long id) throws DaoException {
        final String query = "UPDATE user SET state = 'deleted' WHERE id = ?";
        Transaction tr = new Transaction(conn);
        tr.transactionWrapper(c -> {
            DaoImpl<User> dao = new DaoImpl<>(c, logger);
            dao.delete(id, query);
        });
    }

    @Override
    public List<User> findByPattern(String what, String searchBy, String sortBy, int num, int page)
            throws ServiceException, DaoException {

        SearchSortColumns.check(searchBy, "Search");
        SearchSortColumns.check(sortBy, "Sort");

        final String query = "SELECT * FROM user WHERE " + searchBy + " LIKE ? ORDER BY " + sortBy + " LIMIT ? OFFSET ?";
        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper(c -> {
            DaoImpl<User> dao = new DaoImpl<>(c, logger);
            return dao.findByPattern(what, num, page, query, this::parse);
        });
    }

    @Override
    public int findByPatternCount(String what, String searchBy, String sortBy) throws ServiceException, DaoException {
        logger.debug("start");
        SearchSortColumns.check(searchBy, "Search");
        SearchSortColumns.check(sortBy, "Sort");
        final String query = "SELECT COUNT(*) FROM user WHERE " + searchBy + " LIKE ?";

        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper(c -> {
           DaoImpl<User> dao = new DaoImpl<>(c, logger);
           return dao.count(what, query);
        });
    }
}
