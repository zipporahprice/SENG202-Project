package seng202.team0.repository;

import org.apache.commons.lang3.NotImplementedException;
import seng202.team0.models.Point;
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
public class CrashDAO {
    private final DatabaseManager databaseManager;
    private final Connection connection;

    // TODO think about database manager and how to get connection
    public CrashDAO() {
        databaseManager = new DatabaseManager();
        connection = databaseManager.getConnection();
    }

    // TODO implement this
    public List<Point> getAll() {
        List<Point> pointsList = new ArrayList<Point>();

        return pointsList;
    }

    // TODO implement this
    public Point getOne() {
        throw new NotImplementedException();
    }

    private void addPointToPreparedStatement(PreparedStatement ps, Point pointToAdd) throws SQLException {
        ps.setInt(1, pointToAdd.getObjectId());
        ps.setInt(2, pointToAdd.getSpeedLimit());
        ps.setInt(3, pointToAdd.getCrashYear());
        ps.setString(4, pointToAdd.getCrashLocation1());
        ps.setString(5, pointToAdd.getCrashLocation2());
        ps.setString(6, pointToAdd.getRegion());
        ps.setString(7, pointToAdd.getWeather());
        ps.setFloat(8, pointToAdd.getLongitude());
        ps.setFloat(9, pointToAdd.getLatitude());
        ps.setBoolean(10, pointToAdd.isBicycleInvolved());
        ps.setBoolean(11, pointToAdd.isBusInvolved());
        ps.setBoolean(12, pointToAdd.isCarInvolved());
        ps.setBoolean(13, pointToAdd.isHoliday());
        ps.setBoolean(14, pointToAdd.isMopedInvolved());
        ps.setBoolean(15, pointToAdd.isMotorcycleInvolved());
        ps.setBoolean(16, pointToAdd.isParkedVehicleInvolved());
        ps.setBoolean(17, pointToAdd.isPedestrianInvolved());
        ps.setBoolean(18, pointToAdd.isSchoolBusInvolved());
        ps.setBoolean(19, pointToAdd.isTrainInvolved());
        ps.setBoolean(20, pointToAdd.isTruckInvolved());
    }

    // TODO implement this
    public void addOne(Point pointToAdd) throws SQLException {
        String sql = "INSERT INTO crashes (object_id, speed_limit, crash_year, " +
                "crash_location1, crash_location2, region, weather, " +
                "longitude, latitude, bicycle_involved, bus_involved, " +
                "car_involved, holiday, moped_involved, motorcycle_involved, " +
                "parked_vehicle_involved, pedestrian_involved, " +
                "school_bus_involved, train_involved, train_involved) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        PreparedStatement ps = connection.prepareStatement(sql);

        // Adding all values of point to sql statement
        addPointToPreparedStatement(ps, pointToAdd);

        ps.executeUpdate();
    }

    // TODO implement this
    public void addMultiple(List<Point> toAdd) throws SQLException {
        String sql = "INSERT OR IGNORE INTO crashes (object_id, speed_limit, crash_year, " +
                "crash_location1, crash_location2, region, weather, " +
                "longitude, latitude, bicycle_involved, bus_involved, " +
                "car_involved, holiday, moped_involved, motorcycle_involved, " +
                "parked_vehicle_involved, pedestrian_involved, " +
                "school_bus_involved, train_involved, train_involved) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        PreparedStatement ps = connection.prepareStatement(sql);
        connection.setAutoCommit(false);

        for (Point pointToAdd : toAdd) {
            addPointToPreparedStatement(ps, pointToAdd);
            ps.addBatch();
        }

        ps.executeBatch();
    }
    // TODO implement this
    public void delete(int objectId) {
        throw new NotImplementedException();
    }

    // TODO implement this
    public void update(Point toUpdate) {
        throw new NotImplementedException();
    }

    // TODO remove manual testing
    public static void main(String[] args) throws IOException, SQLException {
        CrashDAO crashDAO = new CrashDAO();
        URL url = Thread.currentThread().getContextClassLoader().getResource("manual_testing_files/test_crash.csv");
        File file = new File(url.getPath());
        CrashCSVImporter importer = new CrashCSVImporter();
        List<Point> pointList = importer.pointListFromFile(file);
        crashDAO.addOne(pointList.get(0));
    }
}
