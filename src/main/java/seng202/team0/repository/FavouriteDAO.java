package seng202.team0.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.models.Favourite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that communicates with the favourites table in the database through SQL queries.
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
public class FavouriteDAO implements DAOInterface<Favourite> {
    private static final Logger log = LogManager.getLogger(FavouriteDAO.class);
    private final DatabaseManager databaseManager;

    /**
     * Creates a new FavouriteDAO object and creates reference to database
     */
    public FavouriteDAO() { this.databaseManager = DatabaseManager.getInstance(); }

    /**
     * Gets all favourite routes from the database
     * @return List of Favourite objects
     */
    @Override
    public List<Favourite> getAll() {
        List<Favourite> favourites = new ArrayList<>();
        String sql = "SELECT * FROM favourites";
        try (Connection conn = databaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Favourite favourite = favouriteFromResultSet(rs);
                assert favourite != null;
                favourites.add(favourite);
            }
            return favourites;
        } catch (AssertionError | SQLException e) {
            log.error(e);
            return new ArrayList<>();
        }
    }

    /**
     * Gets one Favourite object from the database using given ID
     * @param id int identifier for a Favourite object
     * @return Favourite object corresponding to id
     */
    @Override
    public Favourite getOne(int id) {
        Favourite favourite = null;
        String sql = "SELECT * FROM favourites WHERE id=?";

        // Connect to database and gets the corresponding Favourite
        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    favourite = favouriteFromResultSet(rs);
                }
                assert favourite != null;
                return favourite;
            }
        } catch (AssertionError | SQLException sqlException) {
            log.error(sqlException);
            return null;
        }
    }

    /**
     * Adds a given Favourite object to a prepared statement
     * @param ps PreparedStatement being added to
     * @param toAdd Favourite object to be added
     */
    public void addToPreparedStatement(PreparedStatement ps, Favourite toAdd) {
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

    /**
     * Adds a single given Favourite to the database
     * @param toAdd Favourite object to be added
     */
    @Override
    public void addOne(Favourite toAdd) {
        try {
            String sql = "INSERT INTO favourites (start_address, end_address, start_lat, start_lng," +
                    "end_lat, end_lng, filters) values (?,?,?,?,?,?,?);";
            Connection conn = databaseManager.connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            addToPreparedStatement(ps, toAdd);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    // TODO think about use for this. Will a user want to import a previous favourites csv file?
    /**
     * Adds a given list of Favourite objects to the database
     * @param toAdd List of Favourites to be added
     */
    public void addMultiple(List<Favourite> toAdd) {
        try {
            String sql = "INSERT OR IGNORE INTO favourites (start_address, end_address, start_lat, start_long" +
                    "end_lat, end_long, filters) values (?,?,?,?,?,?,?);";
            Connection conn = databaseManager.connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            conn.setAutoCommit(false);
            for (Favourite favToAdd : toAdd) {
                addToPreparedStatement(ps, favToAdd);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    /**
     * Deletes a single Favourite object from the database using given objectId
     * @param id ID of Favourite to be deleted
     */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM sales WHERE id=?";
        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    // TODO Implement if we want to give users functionality to update a saved Favourite
    /**
     * Updates a given Favourite within the database
     * @param toUpdate Favourite object to be updated
     */
    @Override
    public void update(Favourite toUpdate) {
        log.error(new NotImplementedException());
    }

    /**
     * Takes a ResultSet object and creates a Favourite object
     * from the data of the row with corresponding column names.
     * @param rs ResultSet from executing SQL query
     * @return Favourite object with the current row result set is at
     */
    private Favourite favouriteFromResultSet(ResultSet rs) {
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
}
