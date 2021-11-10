package com.epam.java2021.library.dao.daoImpl.mysql;

import com.epam.java2021.library.dao.AbstractDao;
import com.epam.java2021.library.entity.entityImpl.EditRecord;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EditRecordDao implements AbstractDao<EditRecord> {
    private static final Logger logger = LogManager.getLogger(EditRecordDao.class);
    private static final int START = 1;
    private final DaoImpl<EditRecord> daoImpl;
    private final Connection conn;

    public EditRecordDao(Connection conn) {
        this.conn = conn;
        daoImpl = new DaoImpl<>(conn, "edit record");
    }

    private static class SQLQuery {
        public static final String SELECT = "SELECT * FROM editing_history LIMIT ? OFFSET ?";
        private static final String CREATE = "INSERT INTO editing_history VALUES (DEFAULT, DEFAULT, ?, ?, ?)";
        private static final String READ = "SELECT * FROM editing_history WHERE id = ?";
        private static final String UPDATE = "UPDATE editing_history SET edit_by = ?, description = ?, remark = ?";
        //private static final String DELETE = "DELETE ";
    }

    @Override
    public void create(EditRecord record) throws DaoException {
        daoImpl.create(record, logger, SQLQuery.CREATE, this::fillStatement);
    }

    private void fillStatement(EditRecord record, PreparedStatement ps) throws SQLException {
        int i = START;
        ps.setLong(i++, record.getEditBy());
        ps.setString(i++, record.getDescription());
        ps.setString(i++, record.getRemark());
    }

    @Override
    public EditRecord read(long id) throws DaoException {
        return daoImpl.read(id, logger, SQLQuery.READ, this::parse);
    }

    private EditRecord parse(ResultSet rs) throws SQLException {
        EditRecord.Builder builder = new EditRecord.Builder();
        builder.setId(rs.getInt("id"));
        builder.setCreated(rs.getDate("created"));
        builder.setEditBy(rs.getLong("edit_by"));
        builder.setDescription(rs.getString("description"));
        builder.setRemark(rs.getString("remark"));
        return builder.build();
    }

    @Override
    public void update(EditRecord entity) throws DaoException {
        daoImpl.update(entity, logger, SQLQuery.UPDATE, this::fillStatement);
    }

    @Override
    public void delete(EditRecord entity) throws DaoException {
        //daoImpl.delete(entity, logger, SQLQuery.DELETE);
        throw new DaoException("Deleting for edit records isn't supported by a intention");
    }

    @Override
    public List<EditRecord> getRecords(int page, int amount) throws DaoException {
        return daoImpl.getRecords(page, amount, logger, SQLQuery.SELECT, this::parse);
    }
}
