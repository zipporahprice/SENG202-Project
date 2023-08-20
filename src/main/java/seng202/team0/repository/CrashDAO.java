package seng202.team0.repository;

import org.apache.commons.lang3.NotImplementedException;
import seng202.team0.models.Crash;
import seng202.team0.io.CrashCSVImporter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        List<Crash> pointsList = new ArrayList<Crash>();

        return pointsList;
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
        throw new NotImplementedException();
    }

    /**
     * Adds given point to the prepared statement. Used by addOne and addMultiple functions.
     * @param ps prepared statement to add values into
     * @param crashToAdd crash object to add to database
     * @throws SQLException throws if added parameters do not match values
     */
    private void addPointToPreparedStatement(PreparedStatement ps, Crash crashToAdd) throws SQLException {
        ps.setInt(1, crashToAdd.getObjectId());
        ps.setInt(2, crashToAdd.getSpeedLimit());
        ps.setInt(3, crashToAdd.getCrashYear());
        ps.setString(4, crashToAdd.getCrashLocation1());
        ps.setString(5, crashToAdd.getCrashLocation2());
        ps.setString(6, crashToAdd.getRegion());
        ps.setString(7, crashToAdd.getWeather());
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
     * @param pointToAdd object of type T to add
     * @throws SQLException throws if added parameters do not match values
     */
    @Override
    public void addOne(Crash pointToAdd) throws SQLException {
        String sql = "INSERT INTO crashes (object_id, speed_limit, crash_year, " +
                "crash_location1, crash_location2, region, weather, " +
                "longitude, latitude, bicycle_involved, bus_involved, " +
                "car_involved, holiday, moped_involved, motorcycle_involved, " +
                "parked_vehicle_involved, pedestrian_involved, " +
                "school_bus_involved, train_involved, train_involved) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        Connection conn = databaseManager.connect();
        PreparedStatement ps = conn.prepareStatement(sql);

        // Adding all values of point to sql statement
        addPointToPreparedStatement(ps, pointToAdd);

        // TODO have a think about key and how to add the objectId as key
        ps.executeUpdate();
    }

    /**
     * Adds a list of crashes to the database
     * @param toAdd crashes to add
     * @throws SQLException throws if added parameters do not match values
     */
    public void addMultiple(List<Crash> toAdd) throws SQLException {
        String sql = "INSERT OR IGNORE INTO crashes (object_id, speed_limit, crash_year, " +
                "crash_location1, crash_location2, region, weather, " +
                "longitude, latitude, bicycle_involved, bus_involved, " +
                "car_involved, holiday, moped_involved, motorcycle_involved, " +
                "parked_vehicle_involved, pedestrian_involved, " +
                "school_bus_involved, train_involved, train_involved) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        Connection conn = databaseManager.connect();
        PreparedStatement ps = conn.prepareStatement(sql);
        conn.setAutoCommit(false);

        for (Crash pointToAdd : toAdd) {
            addPointToPreparedStatement(ps, pointToAdd);
            ps.addBatch();
        }

        ps.executeBatch();
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
