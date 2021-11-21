package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.impl.mysql.util.EntityParser;
import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.Logger;
import com.epam.java2021.library.dao.impl.mysql.util.StatementFiller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DaoImpl<T extends Entity> {
    public static final int START = 1;
    private final Connection conn;
    private final Logger logger;

    public DaoImpl(Connection conn, Logger logger) {
        this.conn = conn;
        this.logger = logger;
    }

    private void logAndThrow(SQLException e) throws DaoException {
        logger.error(e.getMessage());
        throw new DaoException(e.getMessage(), e);
    }

    public static String escapeForLike(String param) {
        String result = param.replace("!", "!!").replace("%", "!%").replace("_", "!_").replace("[", "![");
        return "%" + result + "%";
    }

    public void create(T entity, String query, StatementFiller<T> filler) throws DaoException {
        logger.trace("Create request: entity={}, query={}", entity, query);
        try(PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            filler.accept(entity, ps);
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setId(rs.getLong(START));
                    }
                }
                logger.info("New entity added: id={}", entity.getId());
            }
        } catch (SQLException e) {
            logAndThrow(e);
        }
    }

    public T read(long id, String query, EntityParser<T> parser) throws DaoException {
        logger.trace("Read request: id={}, query={}", id, query);
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(START, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return parser.accept(rs);
                }
            }
        } catch (SQLException e) {
            logAndThrow(e);
        }
        return null;
    }

    public int count(String pattern, String query) throws DaoException {
        logger.trace("request: pattern={}, query={}", pattern, query);
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(START, escapeForLike(pattern));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(START);
                }
            }
        } catch (SQLException e) {
            logAndThrow(e);
        }
        return -1;
    }

    public void update(T entity, String query, StatementFiller<T> filler) throws DaoException {
        logger.trace("Update request: entity={}, query={}", entity, query);
        try(PreparedStatement ps = conn.prepareStatement(query)) {
            filler.accept(entity, ps);
            if (ps.executeUpdate() > 0) {
                logger.info("Successful update: id={}", entity.getId());
            }
        } catch (SQLException e) {
            logAndThrow(e);
        }
    }

    public void delete(T entity, String query) throws DaoException {
        logger.trace("Delete request: entity={}, query={}", entity, query);
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(START, entity.getId());
            if (ps.executeUpdate() > 0) {
                logger.info("Successful deleting: id={}", entity.getId());
            }
        } catch (SQLException e) {
            logAndThrow(e);
        }
    }

    public List<T> findByPattern(String pattern, int num, int page, String query, EntityParser<T> parser)
            throws DaoException {
        logger.trace("Find by pattern request: pattern={}, query={}, num={}, page={}",
                 pattern, query, num, page);
        List<T> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            int i = START;
            ps.setString(i++, escapeForLike(pattern));
            ps.setInt(i++, num);
            ps.setInt(i++, (page - 1) * num);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(parser.accept(rs));
                }
            }
        } catch (SQLException e) {
            logAndThrow(e);
        }
        return list;
    }


    public void updateLongField(long id1, long id2, String query) throws DaoException {
        logger.trace("Update long field request: id1={}, id2={}, query={}", id1, id2, query);
        try(PreparedStatement ps = conn.prepareStatement(query)) {
            int i = START;
            ps.setLong(i++, id1);
            ps.setLong(i++, id2);
            if (ps.executeUpdate() > 0) {
                logger.info("Successful update: id1={}, id2={}", id1, id2);
            }
        } catch (SQLException e) {
            logAndThrow(e);
        }
    }


    // TODO check if needed
    public List<T> getRecords(int num, int page, String query, EntityParser<T> parser) throws DaoException {
        logger.trace("getRecord request: num={}, page={}, {}", num, page, query);
        List<T> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            int i = START;
            ps.setInt(i++, num);
            ps.setInt(i, page * num);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(parser.accept(rs));
                }
            }
        } catch (SQLException e) {
            logAndThrow(e);
        }
        return list;
    }

    // TODO check if needed
    public T findByUniqueString(String lookUp, String query, EntityParser<T> parser) throws DaoException {
        logger.trace("findByString request: key={}, {}", lookUp, query);
        T result = null;
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(START, lookUp);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = parser.accept(rs);
                }
            }
        } catch (SQLException e) {
            logAndThrow(e);
        }
        return result;
    }



    public List<T> findById(long id, String query, EntityParser<T> parser) throws DaoException {
        logger.trace("findById request: id={}, {}", id, query);
        List<T> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(START, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(parser.accept(rs));
                }
            }
        } catch (SQLException e) {
            logAndThrow(e);
        }
        return list;
    }
}
