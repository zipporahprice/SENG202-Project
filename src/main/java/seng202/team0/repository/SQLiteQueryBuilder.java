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

    /**
     * Takes a comma separated string of columns and appends to current query
     * @param columns
     * @return SQLiteQueryBuilder instance to chain methods
     */
    public SQLiteQueryBuilder select(String columns) {
        query.append("SELECT ").append(columns).append(" ");
        return this;
    }

    /**
     * Takes a table name to query data from
     * @param table
     * @return SQLiteQueryBuilder instance to chain methods
     */
    public SQLiteQueryBuilder from(String table) {
        query.append("FROM ").append(table).append(" ");
        return this;
    }

    /**
     * Takes a comma separated string of conditions and appends to current query
     * @param conditions
     * @return SQLiteQueryBuilder instance to chain methods
     */
    public SQLiteQueryBuilder where(String conditions) {
        query.append("WHERE ").append(conditions).append(" ");
        return this;
    }

    // TODO have a think about how we want the data to come back to us as, currently have as a list
    // TODO List might not be the best way to have it
    /**
     * Takes the query in the builder object and returns a list of all data points in a List object.
     * @return list of all data points from the current query string
     */
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
