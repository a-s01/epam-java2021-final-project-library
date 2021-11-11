package com.epam.java2021.library.testutil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.Properties;

public class DBManager {
    private static final Logger logger = LogManager.getLogger("TEST");
    private static final String PROPERTIES_FILE = "test_db.properties";
    private static final String URL_PROPERTY = "connection.url";

    private DBManager() {}

    private static class DBManagerHolder {
        private static final DBManager INSTANCE = new DBManager();
        private static final String URL;
        static {
            Properties prop = new Properties();
            try (Reader r = new FileReader(PROPERTIES_FILE)) {
                prop.load(r);
            } catch (IOException e) {
                logger.fatal("Unable to read connection data for test DB: " + e.getMessage());
            }
            URL = prop.getProperty(URL_PROPERTY);
        }
    }

    public static DBManager getInstance() {
        return DBManagerHolder.INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DBManagerHolder.URL);
    }

    public boolean read(Connection conn, String table, String column, String value) throws SQLException {
        final String sql = "SELECT * FROM %s WHERE %s = ?";
        try (PreparedStatement ps = conn.prepareStatement(prepareQuery(sql, table, column))) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public String readField(Connection conn, String retrieveField, String table, String column, String value) throws SQLException {
        final String sql = "SELECT %s FROM %s WHERE %s = ?";
        String result = "";
        try (PreparedStatement ps = conn.prepareStatement(prepareQuery(sql, retrieveField, table, column))) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = rs.getString(1);
                }
            }
        }
        return result;
    }

    public boolean delete(Connection conn, String table, String emailColumn, String email) throws SQLException {
        final String sql = "DELETE from %s WHERE %s = ?";
        try (PreparedStatement ps = conn.prepareStatement(prepareQuery(sql, table, emailColumn))) {
            ps.setString(1, email);
            if (ps.executeUpdate() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean execute(Connection conn, String sql) throws SQLException {
        try (Statement st = conn.createStatement()) {
            return st.execute(sql);
        }
    }

    private static String prepareQuery(String sql, String... args) {
        return String.format(sql, (Object[]) args);
    }
}
