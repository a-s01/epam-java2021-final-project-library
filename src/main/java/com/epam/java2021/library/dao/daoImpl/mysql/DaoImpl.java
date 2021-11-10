package com.epam.java2021.library.dao.daoImpl.mysql;

import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DaoImpl<T extends Entity> {
    private static final int START = 1;
    private final Connection conn;
    private final String entityName;

    public DaoImpl(Connection conn, String entityName) {
        this.conn = conn;
        this.entityName = entityName;
    }

    interface StatementFiller<T extends Entity> {
        void accept(T entity, PreparedStatement ps) throws SQLException;
    }

    interface EntityParser<T extends Entity> {
        T accept(ResultSet rs) throws SQLException, DaoException;
    }

    public static String escapeForLike(String param) {
        return param.replace("!", "!!").replace("%", "!%").replace("_", "!_").replace("[", "![");
    }

    public void create(T entity, Logger logger, String query, StatementFiller<T> filler) throws DaoException {
        logger.trace(entity);
        try(PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            filler.accept(entity, ps);
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setId(rs.getLong(START));
                    }
                }
                logger.info("New " + entityName + " added: " + entity.getId());
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoException("Errors occurred during creating " + entityName + " record in DB", e);
        }
    }

    public T read(long id, Logger logger, String query, EntityParser<T> parser) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(START, id);
            try (ResultSet rs = ps.executeQuery()) {
                return parser.accept(rs);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoException("Errors in reading " + entityName + " by id " + id, e);
        }
    }

    public void update(T entity, Logger logger, String query, StatementFiller<T> filler) throws DaoException {
        logger.trace(entity);
        try(PreparedStatement ps = conn.prepareStatement(query)) {
            filler.accept(entity, ps);
            if (ps.executeUpdate() > 0) {
                logger.info("Successful update of " + entityName + " " + entity.getId());
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoException("Errors in updating " + entityName + " " + entity.getId(), e);
        }
    }

    public void delete(T entity, Logger logger, String query) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(START, entity.getId());
            if (ps.executeUpdate() > 0) {
                logger.info("Successful deleting of " + entityName + " " + entity.getId());
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoException("Errors in deleting " + entityName + entity.getId(), e);
        }
    }

    public List<T> getRecords(int page, int amount, Logger logger, String query, EntityParser<T> parser) throws DaoException {
        List<T> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            int i = START;
            ps.setInt(i++, amount);
            ps.setInt(i, page * amount);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(parser.accept(rs));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoException("Error in getting " + entityName + " list", e);
        }
        return list;
    }

    public T findByString(String lookUp, Logger logger, String query, EntityParser<T> parser) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(START, lookUp);
            try (ResultSet rs = ps.executeQuery()) {
                return parser.accept(rs);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoException("Errors in finding " + entityName + " by " + lookUp, e);
        }
    }

    public List<T> findByPattern(String pattern, Logger logger, String query, EntityParser<T> parser) throws DaoException {
        List<T> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(START, escapeForLike(pattern));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(parser.accept(rs));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoException("Error in finding " + entityName + " by pattern " + pattern, e);
        }
        return list;
    }
}
