package com.epam.java2021.library.testutil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class SQLScriptRunner {
    private static final String FILE = "sql/library-test-db-init.sql";
    private static final SQLScriptRunner INSTANCE = new SQLScriptRunner();
    private static final DBManager dbManager = DBManager.getInstance();
    private final List<String> queries;

    private SQLScriptRunner() {
        queries = loadScript();
    }

    private List<String> loadScript() {
        List<String> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals("") || line.startsWith("-- ")) {
                    continue;
                }

                result.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static SQLScriptRunner getInstance() {
        return INSTANCE;
    }

    public void executeScript() throws SQLException {

        if (queries.size() == 0) {
            throw new NoSuchElementException("Script " + FILE + " was not read or is empty");
        }
        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            try (Statement st = conn.createStatement()) {
                for (String query : queries) {
                    st.addBatch(query);
                }
                st.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
