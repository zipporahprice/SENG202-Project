package seng202.team0.repository;

import seng202.team0.models.Crash;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLiteQueryBuilder {
    private final DatabaseManager databaseManager;
    private static StringBuilder query;
    private List<String> selectedColumns;

    private SQLiteQueryBuilder() {
        this.databaseManager = DatabaseManager.getInstance();
        this.query = new StringBuilder();
        this.selectedColumns = new ArrayList<String>();
    }

    // TODO reasoning, done for readability and chaining easier
    public static SQLiteQueryBuilder create() {
        return new SQLiteQueryBuilder();
    }

    public SQLiteQueryBuilder select(String columns) {
        query.append("SELECT ").append(columns).append(" ");
        return this;
    }

    public SQLiteQueryBuilder from(String table) {
        query.append("FROM ").append(table).append(" ");
        return this;
    }

    public SQLiteQueryBuilder where(String conditions) {
        query.append("WHERE ").append(conditions).append(" ");
        return this;
    }

    // TODO have a think about how we want the data to come back to us as, currently have as a list
    // TODO List might not be the best way to have it
    public List build() {
        List data = new ArrayList<HashMap>();
        try (Connection conn = databaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query.toString())) {
            while (rs.next()) {
                // TODO have as object since we do not know if type is string, int, etc.
                HashMap temp = new HashMap<String, Object>();
                for (String column: selectedColumns) {
                    temp.put(column, rs.getObject(column));
                }
                data.add(temp);
            }
        } catch (SQLException e) {
            // TODO do something better with error catching
            System.out.println(e);
        }

        return data;
    }
}
