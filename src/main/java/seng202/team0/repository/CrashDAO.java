package seng202.team0.repository;

import org.apache.commons.lang3.NotImplementedException;
import seng202.team0.models.Crash;
import seng202.team0.io.CrashCSVImporter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that communicates with database's crashes table through SQL queries.
 * @author Neil Alombro
 */
public class CrashDAO implements DAOInterface<Crash> {
    private final DatabaseManager databaseManager;

    /**
     * Creates a new CrashDAO object and gets a reference to the database
     */
    public CrashDAO() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    /**
     * Gets all crashes in the database
     *
     * @return a list of all crashes
     */
    // TODO implement this
    @Override
    public List<Crash> getAll() {
        List<Crash> crashes = new ArrayList<>();
        String sql = "SELECT * FROM crashes";
        try (Connection conn = databaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                crashes.add(new Crash(
                        rs.getInt("object_id"),
                        rs.getInt("speed_limit"),
                        rs.getInt("crash_year"),
                        rs.getString("crash_location1"),
                        rs.getString("crash_location2"),
                        rs.getString("severity"),
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
                        rs.getBoolean("truck_involved")));
            }
            return crashes;
        } catch (SQLException sqlException) {
            System.out.println(sqlException);
            return new ArrayList<>();
        }

    }

    /**
     * Gets an individual crash from database by id
     *
     * @param id id of crash to get
     * @return crash from database that matches id
     */
    // TODO implement this
    @Override
    public Crash getOne(int id) {
        Crash crash = null;
        String sql = "SELECT * FROM crashes WHERE object_id=?";
        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    crash = new Crash(
                            rs.getInt("object_id"),
                            rs.getInt("speed_limit"),
                            rs.getInt("crash_year"),
                            rs.getString("crash_location1"),
                            rs.getString("crash_location2"),
                            rs.getString("severity"),
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
                }
                return crash;
            }
        } catch (SQLException sqlException) {
            System.out.println(sqlException);
            return null;
        }

    }

    /**
     * Adds given point to the prepared statement. Used by addOne and addMultiple functions.
     * @param ps prepared statement to add values into
     * @param crashToAdd crash object to add to database
     * @throws SQLException throws if added parameters do not match values
     */
    private void addPointToPreparedStatement(PreparedStatement ps, Crash crashToAdd) throws SQLException {
        ps.setInt(1, crashToAdd.getSpeedLimit());
        ps.setInt(2, crashToAdd.getCrashYear());
        ps.setString(3, crashToAdd.getCrashLocation1());
        ps.setString(4, crashToAdd.getCrashLocation2());
        ps.setInt(5, crashToAdd.getSeverity().getValue());
        ps.setString(6, crashToAdd.getRegion().getName());
        ps.setString(7, crashToAdd.getWeather().getName());
        ps.setFloat(8, crashToAdd.getLongitude());
        ps.setFloat(9, crashToAdd.getLatitude());
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
    }

    /**
     * Adds one crash to database
     * @param crashToAdd object of type T to add
     * @throws SQLException throws if added parameters do not match values
     */
    @Override
    public void addOne(Crash crashToAdd) throws SQLException {
        String sql = "INSERT INTO crashes (speed_limit, crash_year, " +
                "crash_location1, crash_location2, severity, region, weather, " +
                "longitude, latitude, bicycle_involved, bus_involved, " +
                "car_involved, holiday, moped_involved, motorcycle_involved, " +
                "parked_vehicle_involved, pedestrian_involved, " +
                "school_bus_involved, train_involved, truck_involved) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            addPointToPreparedStatement(ps, crashToAdd);

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            int insertId = -1;
            if (rs.next()) {
                insertId = rs.getInt(1);
            }
            System.out.println(insertId);
        } catch (SQLException sqlException) {
            System.out.println(sqlException);
        }
        Connection conn = databaseManager.connect();
        PreparedStatement ps = conn.prepareStatement(sql);

        // Adding all values of point to sql statement
        addPointToPreparedStatement(ps, crashToAdd);

        // TODO have a think about key and how to add the objectId as key
        ps.executeUpdate();
    }

    /**
     * Adds a list of crashes to the database
     * @param toAdd crashes to add
     * @throws SQLException throws if added parameters do not match values
     */
    public void addMultiple(List<Crash> toAdd) throws SQLException {
        String sql = "INSERT OR IGNORE INTO crashes (speed_limit, crash_year, " +
                "crash_location1, crash_location2, severity, region, weather, " +
                "longitude, latitude, bicycle_involved, bus_involved, " +
                "car_involved, holiday, moped_involved, motorcycle_involved, " +
                "parked_vehicle_involved, pedestrian_involved, " +
                "school_bus_involved, train_involved, truck_involved) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        Connection conn = databaseManager.connect();
        PreparedStatement ps = conn.prepareStatement(sql);
        conn.setAutoCommit(false);

        for (Crash pointToAdd : toAdd) {
            addPointToPreparedStatement(ps, pointToAdd);
            ps.addBatch();
        }

        ps.executeBatch();
        conn.commit();
    }
    // TODO implement this
    @Override
    public void delete(int objectId) {
        throw new NotImplementedException();
    }

    // TODO implement this
    @Override
    public void update(Crash toUpdate) {
        throw new NotImplementedException();
    }

    // TODO remove manual testing
//    public static void main(String[] args) throws IOException, SQLException {
//        CrashDAO crashDAO = new CrashDAO();
//        URL url = Thread.currentThread().getContextClassLoader().getResource("manual_testing_files/test_crash.csv");
//        File file = new File(url.getPath());
//        CrashCSVImporter importer = new CrashCSVImporter();
//        List<Crash> pointList = importer.pointListFromFile(file);
//        crashDAO.addOne(pointList.get(0));
//    }
}
