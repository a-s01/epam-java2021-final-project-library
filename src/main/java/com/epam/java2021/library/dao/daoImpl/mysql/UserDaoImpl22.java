package com.epam.java2021.library.dao.daoImpl.mysql;

import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.dao.factory.factoryImpl.db.MySQLDaoFactory;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.dao.UserDao;
import com.epam.java2021.library.entity.entityImpl.EditRecord;
import com.epam.java2021.library.entity.entityImpl.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.epam.java2021.library.dao.daoImpl.mysql.DaoImpl.escapeForLike;

public class UserDaoImpl22 implements UserDao {
    private static final IDaoFactoryImpl daoFactory = new MySQLDaoFactory();
    private final Connection conn;
    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);
    private static final int START = 1;

    public UserDaoImpl22(Connection conn) {
        this.conn = conn;
    }

    public static class SQLQuery {
        private SQLQuery() {}

        public static final String CREATE = "INSERT INTO user VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, DEFAULT, ?)";
        public static final String READ = "SELECT * FROM user WHERE id = ?";
        public static final String FIND_BY_EMAIL = "SELECT * FROM user WHERE email = ?";
        public static final String FIND_BY_EMAIL_PATTERN = "SELECT * FROM user WHERE email LIKE ?";
        public static final String SELECT = "SELECT * FROM user ORDER by id LIMIT ? OFFSET ?";
        public static final String UPDATE = "UPDATE user SET email = ?, password = ?, salt = ?, role = ?, state = ?" +
                "fine = ?, name = ?, last_edit_id = ? WHERE id = ?";
        public static final String DELETE = "UPDATE user SET state = 'deleted' WHERE id = ?";
    }

    @Override
    public void create(User user) throws DaoException {
        logger.trace(user);
        try(PreparedStatement ps = conn.prepareStatement(SQLQuery.CREATE, Statement.RETURN_GENERATED_KEYS)) {
            fillStatement(user, ps);
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setId(rs.getLong(START));
                    }
                }
                logger.info("New user added: " + user.getEmail());
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoException("Errors occurred during creating user record in DB", e);
        }
    }

    private int fillStatement(User user, PreparedStatement ps) throws SQLException {
        int i = START;
        ps.setString(i++, user.getEmail());
        ps.setString(i++, user.getPassword());
        ps.setString(i++, user.getSalt());
        ps.setInt(i++, user.getRole().ordinal());
        ps.setInt(i++, user.getState().ordinal());
        ps.setDouble(i++, user.getFine());
        ps.setString(i++, user.getName());
        EditRecord lastEdit = user.getLastEdit();
        if (lastEdit != null) {
            ps.setLong(i++, lastEdit.getId());
        } else {
            ps.setNull(i++, Types.INTEGER);
        }
        return i;
    }

    @Override
    public User read(long id) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(SQLQuery.READ)) {
            ps.setLong(START, id);
            try (ResultSet rs = ps.executeQuery()) {
                return parse(rs);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoException("Errors in reading user by id " + id, e);
        }
    }

    private User parse(ResultSet rs) throws SQLException, DaoException {
        User.Builder builder = new User.Builder();
        builder.setId(rs.getInt("id"));
        builder.setEmail(rs.getString("email"));
        builder.setPassword(rs.getString("password"));
        builder.setSalt(rs.getString("salt"));
        builder.setRole(User.Role.valueOf(rs.getString("role")));
        builder.setState(User.State.valueOf(rs.getString("state")));
        builder.setFine(rs.getDouble("fine"));
        builder.setCreated(rs.getDate("created"));
        long editRecordId = rs.getInt("lastEdit");
        if (editRecordId != 0) {
            EditRecordDao editRecordDao = daoFactory.getEditRecordDao(conn);
            EditRecord lastEdit = editRecordDao.read(editRecordId);
            builder.setLastEdit(lastEdit);
        }
        return builder.build();
    }

    @Override
    public void update(User user) throws DaoException {
        logger.trace(user);
        try(PreparedStatement ps = conn.prepareStatement(SQLQuery.UPDATE)) {
            int nextIndex = fillStatement(user, ps);
            ps.setLong(nextIndex, user.getId());
            if (ps.executeUpdate() > 0) {
                logger.info("Successful update of user " + user.getEmail());
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoException("Errors in updating user " + user.getEmail(), e);
        }
    }

    @Override
    public void delete(User user) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(SQLQuery.DELETE)) {
            ps.setLong(START, user.getId());
            if (ps.executeUpdate() > 0) {
                logger.info("Successful deleting of user " + user.getEmail());
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoException("Errors in deleting user " + user.getEmail(), e);
        }
    }

    @Override
    public List<User> getRecords(int page, int amount) throws DaoException {
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQLQuery.SELECT)) {
            int i = START;
            ps.setInt(i++, amount);
            ps.setInt(i, page * amount);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(parse(rs));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoException("Error in getting user list", e);
        }
        return users;
    }

    @Override
    public User findByEmail(String email) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(SQLQuery.FIND_BY_EMAIL)) {
            ps.setString(START, email);
            try (ResultSet rs = ps.executeQuery()) {
                return parse(rs);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoException("Errors in finding user by email " + email, e);
        }
    }

    @Override
    public List<User> findByEmailPattern(String emailPattern) throws DaoException {
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQLQuery.FIND_BY_EMAIL_PATTERN)) {
            ps.setString(START, escapeForLike(emailPattern));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(parse(rs));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoException("Error in finding user by email pattern " + emailPattern, e);
        }
        return users;
    }
}
