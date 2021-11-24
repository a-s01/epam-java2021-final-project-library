package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.LangDao;
import com.epam.java2021.library.dao.impl.mysql.util.Transaction;
import com.epam.java2021.library.entity.impl.Lang;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LangDaoImpl implements LangDao {
    private static final Logger logger = LogManager.getLogger(LangDaoImpl.class);
    private Connection conn;

    public LangDaoImpl(Connection conn) {
        this.conn = conn;
    }
    public LangDaoImpl() {}

    @Override
    public List<Lang> getAll() throws DaoException {
        final String query = "SELECT * FROM lang";

        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper(c -> {
            DaoImpl<Lang> dao = new DaoImpl<>(c, logger);
            return dao.getRecords(query, this::parse);
        });
    }

    private Lang parse(Connection c, ResultSet rs) throws SQLException {
        Lang.Builder builder = new Lang.Builder();
        builder.setId(rs.getInt("id"));
        builder.setCode(rs.getString("code"));
        return builder.build();
    }

    @Override
    public Lang read(long id) {
        return null;
    }

    @Override
    public Lang read(String code) {
        return null;
    }
}
