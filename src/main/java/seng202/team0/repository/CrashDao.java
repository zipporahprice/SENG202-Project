package seng202.team0.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.models.Crash;
import seng202.team0.models.CrashSeverity;


/**
 * Class that communicates with the SQLite database's crashes table through SQL queries.
 *
 * @author Morgan English
 * @author Angelica Silva
 * @author Christopher Wareing
 * @author Neil Alombro
 * @author Todd Vermeir
 * @author William Thompson
 * @author Zipporah Price
 *
 */
public class CrashDao implements DaoInterface<Crash> {
    private static final Logger log = LogManager.getLogger(CrashDao.class);
    private final DatabaseManager databaseManager;

    /**
     * Creates a new CrashDAO object and gets a reference to the database.
     */
    public CrashDao() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    /**
     * Gets all crashes from crashes table in the SQLite database.
     *
     * @return List of all crashes from SQLite database
     */
    @Override
    public List<Crash> getAll() {
        // Initialise crash list and SQL query statement
        List<Crash> crashes = new ArrayList<>();
        String sql = "SELECT * FROM crashes";

        // Connect to database and keep adding to crash list until the end of query result
        try (Connection conn = databaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Crash crash = crashFromResultSet(rs);
                assert crash != null;
                crashes.add(crash);
            }
            return crashes;
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return new ArrayList<>();
        }
    }

    /**
     * Gets an individual crash from database by id.
     *
     * @param id id of crash to get
     * @return crash from database that matches id
     */
    @Override
    public Crash getOne(int id) {
        // Initialise crash and SQL query statement
        Crash crash = null;
        String sql = "SELECT * FROM crashes WHERE object_id=?";

        // Connect to database manager and run SQL query
        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    crash = crashFromResultSet(rs);
                }
                assert crash != null;
                return crash;
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return null;
        }
    }

    /**
     * Adds given point to the prepared statement. Used by addOne and addMultiple functions.
     *
     * @param ps Prepared statement to add values into
     * @param crashToAdd Crash object to add to database
     */
    private void addPointToPreparedStatement(PreparedStatement ps, Crash crashToAdd) {
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
     * Adds one crash to database.
     *
     * @param crashToAdd Crash object to add
     */
    @Override
    public void addOne(Crash crashToAdd) {
        // SQL statement for adding
        String sql = "INSERT INTO crashes (speed_limit, crash_year, "
                + "crash_location1, crash_location2, severity, region, weather, "
                + "longitude, latitude, bicycle_involved, bus_involved, "
                + "car_involved, holiday, moped_involved, motorcycle_involved, "
                + "parked_vehicle_involved, pedestrian_involved, "
                + "school_bus_involved, train_involved, truck_involved) "
                + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            addPointToPreparedStatement(ps, crashToAdd);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    /**
     * Adds a list of Crash objects to the SQLite database.
     *
     * @param toAdd Crashes to add
     */
    public void addMultiple(List<Crash> toAdd) {
        try {
            String sql = "INSERT OR IGNORE INTO crashes (speed_limit, crash_year, "
                    + "crash_location1, crash_location2, severity, region, weather, "
                    + "longitude, latitude, bicycle_involved, bus_involved, "
                    + "car_involved, holiday, moped_involved, motorcycle_involved, "
                    + "parked_vehicle_involved, pedestrian_involved, "
                    + "school_bus_involved, train_involved, truck_involved) "
                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            Connection conn = databaseManager.connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            conn.setAutoCommit(false);

            for (Crash pointToAdd : toAdd) {
                addPointToPreparedStatement(ps, pointToAdd);
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    // TODO Not implemented. Implement when able to import crashes from personal csv file
    /**
     * Deletes the corresponding objectId row in the crashes table in SQlite database.
     *
     * @param objectId Id of object to delete
     */
    @Override
    public void delete(int objectId) {
        throw new NotImplementedException();
    }

    // TODO Not implemented. Implement when able to import crashes from personal csv file
    /**
     * Takes a Crash object with updates values and updates the corresponding
     * objectId row in the crashes table in the SQLite database.
     *
     * @param toUpdate Crash that needs to be updated
     */
    @Override
    public void update(Crash toUpdate) {
        throw new NotImplementedException();
    }

    /**
     * Takes a ResultSet object and creates a Crash object
     * from the data of the row with corresponding column names.
     *
     * @param rs ResultSet from executing SQL query
     * @return Crash object with the current row result set is at
     */
    private Crash crashFromResultSet(ResultSet rs) {
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
}
