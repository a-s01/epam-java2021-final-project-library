package com.epam.java2021.library.dao.daoImpl.mysql;

import com.epam.java2021.library.service.DependencyHolder;
import com.epam.java2021.library.dao.UserDao;
import com.epam.java2021.library.entity.entityImpl.EditRecord;
import com.epam.java2021.library.entity.entityImpl.User;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;

public class UserDaoImpl implements UserDao {
    private DaoImpl<User> daoImpl;
    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);
    private final DependencyHolder<User> holder = new DependencyHolder<>();

    public void setConnection (Connection conn) {
        daoImpl = new DaoImpl<>(conn, logger, "user");
    }

    @Override
    public DependencyHolder<User> getDependencies() {
        return holder;
    }

    @Override
    public void setLastEdit(User user, EditRecord lastEdit) throws DaoException {
        user.setLastEdit(lastEdit);
        daoImpl.updateLongField(lastEdit.getId(), user, SQLQuery.UPDATE_LAST_EDIT);
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
        private static final String UPDATE_LAST_EDIT = "UPDATE user SET last_edit_id = ? WHERE id = ?";
    }

    @Override
    public void create(User user) throws DaoException {
        daoImpl.create(user, SQLQuery.CREATE, this::fillStatement);
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

    private User parse(ResultSet rs) throws SQLException {
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

        User user = builder.build();
        long editRecordId = rs.getInt("last_edit_id");
        holder.set(user, "editRecordID", editRecordId);
        return user;
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
