package com.epam.java2021.library.dao.daoImpl.mysql;

import com.epam.java2021.library.dao.factory.IDaoFactoryImpl;
import com.epam.java2021.library.dao.factory.factoryImpl.db.MySQLDaoFactory;
import com.epam.java2021.library.dao.UserDao;
import com.epam.java2021.library.entity.entityImpl.EditRecord;
import com.epam.java2021.library.entity.entityImpl.User;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;

public class UserDaoImpl implements UserDao {
    private static final IDaoFactoryImpl daoFactory = new MySQLDaoFactory();
    private Connection conn;
    private DaoImpl<User> daoImpl;
    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);
    private static final int START = 1;

    public void setConnection (Connection conn) {
        this.conn = conn;
        daoImpl = new DaoImpl<>(conn, logger, "user");
    }

    private static class SQLQuery {
        private SQLQuery() {}

        private static final String CREATE = "INSERT INTO user VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, DEFAULT, ?)";
        private static final String READ = "SELECT * FROM user WHERE id = ?";
        private static final String FIND_BY_EMAIL = "SELECT * FROM user WHERE email = ?";
        private static final String FIND_BY_EMAIL_PATTERN = "SELECT * FROM user WHERE email LIKE ?";
        private static final String SELECT = "SELECT * FROM user ORDER by id LIMIT ? OFFSET ?";
        private static final String UPDATE = "UPDATE user SET email = ?, password = ?, salt = ?, role = ?, state = ?, " +
                "fine = ?, name = ?, last_edit_id = ? WHERE id = ?";
        private static final String DELETE = "UPDATE user SET state = 'deleted' WHERE id = ?";
    }

    @Override
    public void create(User user) throws DaoException {
        daoImpl.create(user, SQLQuery.CREATE, this::fillStatement);
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
        return daoImpl.read(id, SQLQuery.READ, this::parse);
    }

    private User parse(ResultSet rs) throws SQLException, DaoException {
        User.Builder builder = new User.Builder();
        builder.setId(rs.getInt("id"));
        builder.setEmail(rs.getString("email"));
        builder.setPassword(rs.getString("password"));
        builder.setSalt(rs.getString("salt"));
        String dbRole = rs.getString("role");
        builder.setRole(User.Role.valueOf(dbRole));
        String dbState = rs.getString("state");
        builder.setState(User.State.valueOf(dbState));
        builder.setFine(rs.getDouble("fine"));
        builder.setCreated(rs.getDate("created"));
        long editRecordId = rs.getInt("last_edit_id");
        logger.trace("last_edit_id = " + editRecordId);
        if (editRecordId != 0) {
            EditRecordDao editRecordDao = daoFactory.getEditRecordDao();
            EditRecord lastEdit = editRecordDao.read(editRecordId);
            builder.setLastEdit(lastEdit);
        }
        return builder.build();
    }

    @Override
    public void update(User user) throws DaoException {
        daoImpl.update(user, SQLQuery.UPDATE,
                (entity, ps) -> {
                    int nextIndex = fillStatement(entity, ps);
                    ps.setLong(nextIndex, entity.getId());
                }
        );
    }

    @Override
    public void delete(User user) throws DaoException {
        daoImpl.delete(user, SQLQuery.DELETE);
    }

    @Override
    public List<User> getRecords(int page, int amount) throws DaoException {
        return daoImpl.getRecords(page, amount, SQLQuery.SELECT, this::parse);
    }

    @Override
    public User findByEmail(String email) throws DaoException {
        return daoImpl.findByUniqueString(email, SQLQuery.FIND_BY_EMAIL, this::parse);
    }

    @Override
    public List<User> findByEmailPattern(String emailPattern) throws DaoException {
        return daoImpl.findByPattern(emailPattern, SQLQuery.FIND_BY_EMAIL_PATTERN, this::parse);
    }
}
