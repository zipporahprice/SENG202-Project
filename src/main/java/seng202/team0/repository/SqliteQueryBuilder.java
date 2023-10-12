package seng202.team0.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Builder class of SQL queries for the SQLite database.
 * Functions to chain include create, select, from, where, and build.
 * Future developments look to create functions for delete, insert, update, group by.
 *
 * @author Angelica Silva
 * @author Christopher Wareing
 * @author Neil Alombro
 * @author Todd Vermeir
 * @author William Thompson
 * @author Zipporah Price
 *
 */

public class SqliteQueryBuilder {
    private static final Logger log = LogManager.getLogger(SqliteQueryBuilder.class);
    private final DatabaseManager databaseManager;
    private static StringBuilder query;
    private final List<String> selectedColumns;

    /**
     * Private instantiate that allows future connections to the database,
     * creates an empty query, and an empty list for columns selected to move to.
     */
    private SqliteQueryBuilder() {
        this.databaseManager = DatabaseManager.getInstance();
        query = new StringBuilder();
        this.selectedColumns = new ArrayList<>();
    }

    /**
     * Create function creates a new instance of the class.
     * Done for readability with function chaining.
     *
     * @return SQLiteQueryBuilder instance to chain methods
     */
    public static SqliteQueryBuilder create() {
        return new SqliteQueryBuilder();
    }

    /**
     * Takes a comma separated string of columns and appends to current query.
     *
     * @param columns Comma separated string of columns or "*" denoting all columns of table
     * @return SQLiteQueryBuilder instance to chain methods
     */
    public SqliteQueryBuilder select(String columns) {
        query.append("SELECT ").append(columns).append(" ");
        String[] columnsWithoutCommas = columns.split(",");

        // Adding columns to selectedColumns
        for (String column : columnsWithoutCommas) {
            selectedColumns.add(column.trim());
        }

        return this;
    }

    /**
     * takes a string of columns of interest and adds to current query.
     *
     * @param columns comma separated string showing all columns we want to group by
     * @return instance to chain methods
     */
    public SqliteQueryBuilder groupBy(String columns) {
        query.append("GROUP BY ").append(columns).append(" ");
        String[] columnsWithoutCommas = columns.split(",");

        for (String column : columnsWithoutCommas) {
            selectedColumns.add(column.trim());
        }

        return this;
    }

    /**
     * Takes a table name to query data from.
     * Note: Updates selected columns list from the table's metadata if all columns selected.
     *
     * @param table String of table name
     * @return SQLiteQueryBuilder instance to chain methods
     */
    public SqliteQueryBuilder from(String table) {
        query.append("FROM ").append(table).append(" ");

        // If "*" is selected, update columns to all columns of the table
        if (selectedColumns.contains("*")) {
            try (Connection conn = databaseManager.connect()) {
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet rs = metaData.getColumns(null, null, table, null);

                selectedColumns.clear();
                while (rs.next()) {
                    selectedColumns.add(rs.getString("COLUMN_NAME"));
                }
            } catch (SQLException sqlException) {
                log.error(sqlException);
            }
        }

        return this;
    }

    /**
     * Takes a comma separated string of conditions and appends to current query.
     *
     * @param conditions Comma separated string of conditions
     * @return SQLiteQueryBuilder instance to chain methods
     */
    public SqliteQueryBuilder where(String conditions) {
        query.append("WHERE ").append(conditions).append(" ");
        return this;
    }

    // TODO have a think about how we want the data to come back to us as, currently have as a list
    /**
     * Takes the query in the builder object and returns a list of all data points in a List object.
     *
     * @return List of all data points from the current query string
     */
    public List build() {
        List data = new ArrayList<HashMap>();
        try (Connection conn = databaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query.toString())) {
            while (rs.next()) {
                // TODO have as object since we do not know if type is string, int, etc.
                HashMap temp = new HashMap<String, Object>();
                for (String column : selectedColumns) {
                    temp.put(column, rs.getObject(column));
                }
                data.add(temp);
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }

        return data;
    }

    /**
     * Getter method for query.
     *
     * @return Query string at the current state of method chaining
     */
    public String getQuery() {
        return query.toString();
    }
}
