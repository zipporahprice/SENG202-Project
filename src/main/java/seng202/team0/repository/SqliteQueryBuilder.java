package seng202.team0.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.models.Crash;
import seng202.team0.models.CrashSeverity;
import seng202.team0.models.Favourite;

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
    private boolean allColumnsFromTable = false;
    private String table;

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
     * Takes a table to append to and appends to current query.
     *
     * @param table table to append to
     * @return SQLiteQueryBuilder instance to chain methods
     */
    public SqliteQueryBuilder insert(String table) {
        String columns = "";

        if (table.equals("favourites")) {
            columns = " (start_address, end_address, start_lat, start_lng, " +
                    "end_lat, end_lng, filters) values (?,?,?,?,?,?,?)";
        } else if (table.equals("crashes")) {
            columns = " (speed_limit, crash_year, "
                    + "crash_location1, crash_location2, severity, region, weather, "
                    + "longitude, latitude, bicycle_involved, bus_involved, "
                    + "car_involved, holiday, moped_involved, motorcycle_involved, "
                    + "parked_vehicle_involved, pedestrian_involved, "
                    + "school_bus_involved, train_involved, truck_involved) "
                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        }

        query.append("INSERT INTO " ).append(table).append(columns);
        this.table = table;

        return this;
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
     * Takes a table name to query data from.
     * Note: Updates selected columns list from the table's metadata if all columns selected.
     *
     * @param table String of table name
     * @return SQLiteQueryBuilder instance to chain methods
     */
    public SqliteQueryBuilder from(String table) {
        this.table = table;
        query.append("FROM ").append(table).append(" ");

        // If "*" is selected, update columns to all columns of the table
        if (selectedColumns.contains("*")) {
            allColumnsFromTable = true;

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

    public void buildSetter(List<?> objectsToAdd) {
        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(query.toString());){
            conn.setAutoCommit(false);

            if (!objectsToAdd.isEmpty()) {
                Object firstElement = objectsToAdd.get(0);
                if (firstElement instanceof Crash) {
                    for (Object crash : objectsToAdd) {
                        addCrashToPreparedStatement(ps, (Crash) crash);
                        ps.addBatch();
                    }
                } else if (firstElement instanceof Favourite) {
                    for (Object favourite : objectsToAdd) {
                        addFavouriteToPreparedStatement(ps, (Favourite) favourite);
                        ps.addBatch();
                    }
                }
            }

            ps.executeBatch();
            conn.commit();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }

    }

    /**
     * Adds given point to the prepared statement. Used by addOne and addMultiple functions.
     *
     * @param ps Prepared statement to add values into
     * @param crashToAdd Crash object to add to database
     */
    private void addCrashToPreparedStatement(PreparedStatement ps, Crash crashToAdd) {
        try {
            ps.setInt(1, crashToAdd.getSpeedLimit());
            ps.setInt(2, crashToAdd.getCrashYear());
            ps.setString(3, crashToAdd.getCrashLocation1());
            ps.setString(4, crashToAdd.getCrashLocation2());
            ps.setInt(5, crashToAdd.getSeverity().getValue());
            ps.setString(6, crashToAdd.getRegion().getName());
            ps.setString(7, crashToAdd.getWeather().getName());
            ps.setDouble(8, crashToAdd.getLongitude());
            ps.setDouble(9, crashToAdd.getLatitude());
            ps.setBoolean(10, crashToAdd.isBicycleInvolved());
            ps.setBoolean(11, crashToAdd.isBusInvolved());
            ps.setBoolean(12, crashToAdd.isCarInvolved());
            ps.setBoolean(13, crashToAdd.isHoliday());
            ps.setBoolean(14, crashToAdd.isMopedInvolved());
            ps.setBoolean(15, crashToAdd.isMotorcycleInvolved());
            ps.setBoolean(16, crashToAdd.isParkedVehicleInvolved());
            ps.setBoolean(17, crashToAdd.isPedestrianInvolved());
            ps.setBoolean(18, crashToAdd.isSchoolBusInvolved());
            ps.setBoolean(19, crashToAdd.isTrainInvolved());
            ps.setBoolean(20, crashToAdd.isTruckInvolved());
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    /**
     * Adds a given Favourite object to a prepared statement.
     *
     * @param ps PreparedStatement being added to
     * @param toAdd Favourite object to be added
     */
    public void addFavouriteToPreparedStatement(PreparedStatement ps, Favourite toAdd) {
        try {
            ps.setString(1, toAdd.getStartAddress());
            ps.setString(2, toAdd.getEndAddress());
            ps.setDouble(3, toAdd.getStartLat());
            ps.setDouble(4, toAdd.getStartLong());
            ps.setDouble(5, toAdd.getEndLat());
            ps.setDouble(6, toAdd.getEndLong());
            ps.setString(7, toAdd.getFilters());
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    // TODO have a think about how we want the data to come back to us as, currently have as a list
    /**
     * Takes the query in the builder object and returns a list of all data points in a List object.
     *
     * @return List of all data points from the current query string
     */
    public List<Object> buildGetter() {
        List<Object> data = new ArrayList<>();
        try (Connection conn = databaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query.toString())) {
            while (rs.next()) {
                Object temp = null;
                if (allColumnsFromTable) {
                    if (table.equals("crashes")) {
                        temp = resultsAsCrash(rs);
                    } else if (table.equals("favourites")) {
                        temp = resultsAsFavourite(rs);
                    }
                }
                data.add(temp);
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }

        return data;
    }

    /**
     * Takes in a result set from query and returns the current row as a Hashmap.
     * @param rs Result set from query.
     * @return HashMap with column names as the key and value as an Object.
     */
    private HashMap<String, Object> resultAsHashmap(ResultSet rs) {
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            for (String column : selectedColumns) {
                hashMap.put(column, rs.getObject(column));
            }
            return hashMap;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return null;
        }
    }

    private Crash resultsAsCrash(ResultSet rs) {
        try {
            return new Crash(
                    rs.getInt("object_id"),
                    rs.getInt("speed_limit"),
                    rs.getInt("crash_year"),
                    rs.getString("crash_location1"),
                    rs.getString("crash_location2"),
                    CrashSeverity.intToString(rs.getInt("severity")),
                    rs.getString("region"),
                    rs.getString("weather"),
                    rs.getFloat("longitude"),
                    rs.getFloat("latitude"),
                    rs.getBoolean("bicycle_involved"),
                    rs.getBoolean("bus_involved"),
                    rs.getBoolean("car_involved"),
                    rs.getBoolean("holiday"),
                    rs.getBoolean("moped_involved"),
                    rs.getBoolean("motorcycle_involved"),
                    rs.getBoolean("parked_vehicle_involved"),
                    rs.getBoolean("pedestrian_involved"),
                    rs.getBoolean("school_bus_involved"),
                    rs.getBoolean("train_involved"),
                    rs.getBoolean("truck_involved"));
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return null;
        }
    }

    private Favourite resultsAsFavourite(ResultSet rs) {
        try {
            return new Favourite(
                    rs.getString("start_address"),
                    rs.getString("end_address"),
                    rs.getFloat("start_lat"),
                    rs.getFloat("start_lng"),
                    rs.getFloat("end_lat"),
                    rs.getFloat("end_lng"),
                    rs.getString("filters"));
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return null;
        }
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
