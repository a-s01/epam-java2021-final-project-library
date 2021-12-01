package com.epam.java2021.library.dao.impl.mysql.util;

import com.epam.java2021.library.dao.impl.mysql.func.EntityParser;
import com.epam.java2021.library.dao.impl.mysql.func.StatementFiller;
import com.epam.java2021.library.entity.Entity;
import com.epam.java2021.library.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.epam.java2021.library.constant.Common.*;

public class BaseDao<T extends Entity> {
    private static final Logger logger = LogManager.getLogger(BaseDao.class);
    public static final int START = 1;
    private final Connection conn;

    public BaseDao(Connection conn) {
        this.conn = conn;
        logger.trace("conn={}", conn);
    }

    private void logAndThrow(SQLException e) throws DaoException {
        logger.error(e.getMessage());
        throw new DaoException(e.getMessage(), e);
    }

    public static String escapeForLike(String param) {
        logger.debug(START_MSG);

        String result = param.replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_")
                .replace("[", "![");
        logger.debug(END_MSG);
        return "%" + result + "%";
    }

    public void create(T entity, String query, StatementFiller<T> filler) throws DaoException {
        logger.debug(START_MSG);
        logger.trace("entity={}, query={}", entity, query);

        try(PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            filler.accept(entity, ps);
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setId(rs.getLong(START));
                    }
                }
                logger.info("New entity added: id={}", entity.getId());
            } else {
                logger.info(NO_UPDATE);
            }
        } catch (SQLException e) {
            logAndThrow(e);
        } finally {
            logger.debug(END_MSG);
        }
    }

    public T read(long id, String query, EntityParser<T> parser) throws DaoException {
        logger.debug(START_MSG);
        logger.trace("id={}, query={}", id, query);

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(START, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return parser.accept(conn, rs);
                }
            }
        } catch (SQLException e) {
            logAndThrow(e);
        } finally {
            logger.debug(END_MSG);
        }
        return null;
    }

    public int count(String pattern, String query) throws DaoException {
        logger.debug(START_MSG);
        logger.trace("pattern={}, query={}", pattern, query);

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(START, escapeForLike(pattern));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(START);
                }
            }
        } catch (SQLException e) {
            logAndThrow(e);
        } finally {
            logger.debug(END_MSG);
        }
        return -1;
    }

    public void update(T entity, String query, StatementFiller<T> filler) throws DaoException {
        logger.debug(START_MSG);
        logger.trace("entity={}, query={}", entity, query);
        try(PreparedStatement ps = conn.prepareStatement(query)) {
            filler.accept(entity, ps);
            if (ps.executeUpdate() > 0) {
                logger.info("Successful update: id={}", entity.getId());
            } else {
                logger.info(NO_UPDATE);
            }
        } catch (SQLException e) {
            logAndThrow(e);
        } finally {
            logger.debug(END_MSG);
        }
    }

    public void delete(long id, String query) throws DaoException {
        logger.debug(START_MSG);
        logger.trace("id={}, query={}", id, query);
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(START, id);
            if (ps.executeUpdate() > 0) {
                logger.info("Successful deleting: id={}", id);
            }
        } catch (SQLException e) {
            logAndThrow(e);
        } finally {
            logger.debug(END_MSG);
        }
    }

    public void deleteBound(long id1, long id2, String query) throws DaoException {
        logger.debug(START_MSG);
        logger.trace("id1={}, id2={}, query={}", id1, id2, query);

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            int i = START;
            ps.setLong(i++, id1);
            ps.setLong(i, id2);
            if (ps.executeUpdate() > 0) {
                logger.info(SUCCESS);
            } else {
                logger.info(NO_UPDATE);
            }
        } catch (SQLException e) {
            logAndThrow(e);
        } finally {
            logger.debug(END_MSG);
        }
    }

    public void createBound(long id, T entity, String query, StatementFiller<T> filler) throws DaoException {
        logger.debug(START_MSG);
        logger.trace("id={}, entity={}, query={}", id, entity, query);

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            int i = filler.accept(entity, ps);
            ps.setLong(i, id);
            if (ps.executeUpdate() > 0) {
                logger.info(SUCCESS);
            } else {
                logger.info(NO_UPDATE);
            }
        } catch (SQLException e) {
            logAndThrow(e);
        } finally {
            logger.debug(END_MSG);
        }
    }

    /**
     * page starts from 1
     * @param pattern
     * @param num
     * @param page
     * @param query
     * @param parser
     * @return
     * @throws DaoException
     */
    public List<T> findByPattern(String pattern, int num, int page, String query, EntityParser<T> parser)
            throws DaoException {
        logger.debug(START_MSG);
        logger.trace("pattern={}, query={}, num={}, page={}",
                 pattern, query, num, page);
        List<T> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            int i = START;
            ps.setString(i++, escapeForLike(pattern));
            ps.setInt(i++, num);
            ps.setInt(i, (page - 1) * num);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(parser.accept(conn, rs));
                }
            }
        } catch (SQLException e) {
            logAndThrow(e);
        } finally {
            logger.debug(END_MSG);
        }

        return list;
    }

    public List<T> findByPattern(String pattern, String query, EntityParser<T> parser)
            throws DaoException {
        logger.debug(START_MSG);
        logger.trace("pattern={}, query={}",
                pattern, query);

        List<T> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(START, escapeForLike(pattern));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(parser.accept(conn, rs));
                }
            }
        } catch (SQLException e) {
            logAndThrow(e);
        } finally {
            logger.debug(END_MSG);
        }

        return list;
    }

    public List<T> findByString(String pattern, String query, EntityParser<T> parser)
            throws DaoException {
        logger.debug(START_MSG);
        logger.trace("pattern={}, query={}",
                pattern, query);

        List<T> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(START, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(parser.accept(conn, rs));
                }
            }
        } catch (SQLException e) {
            logAndThrow(e);
        } finally {
            logger.debug(END_MSG);
        }

        return list;
    }

    public void updateBound(long id1, long id2, String query) throws DaoException {
        logger.debug(START_MSG);
        logger.trace("id1={}, id2={}, query={}", id1, id2, query);

        try(PreparedStatement ps = conn.prepareStatement(query)) {
            int i = START;
            ps.setLong(i++, id1);
            ps.setLong(i, id2);
            if (ps.executeUpdate() > 0) {
                logger.info("success");
            }
        } catch (SQLException e) {
            logAndThrow(e);
        } finally {
            logger.debug(END_MSG);
        }
    }


    public List<T> getRecords(String query, EntityParser<T> parser) throws DaoException {
        logger.debug(START_MSG);
        logger.trace("query={}", query);

        List<T> list = new ArrayList<>();
        try (Statement ps = conn.createStatement()) {
            try (ResultSet rs = ps.executeQuery(query)) {
                while (rs.next()) {
                    list.add(parser.accept(conn, rs));
                }
            }
        } catch (SQLException e) {
            logAndThrow(e);
        } finally {
            logger.debug(END_MSG);
        }

        return list;
    }

    public T read(String lookUp, String query, EntityParser<T> parser) throws DaoException {
        logger.debug(START_MSG);
        logger.trace("key={}, {}", lookUp, query);

        T result = null;
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(START, lookUp);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = parser.accept(conn, rs);
                }
            }
        } catch (SQLException e) {
            logAndThrow(e);
        } finally {
            logger.debug(END_MSG);
        }

        return result;
    }

    public List<T> findById(long id, String query, EntityParser<T> parser) throws DaoException {
        logger.debug(START_MSG);
        logger.trace("id={}, {}", id, query);

        List<T> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(START, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(parser.accept(conn, rs));
                }
            }
        } catch (SQLException e) {
            logAndThrow(e);
        } finally {
            logger.debug(END_MSG);
        }

        return list;
    }


}
