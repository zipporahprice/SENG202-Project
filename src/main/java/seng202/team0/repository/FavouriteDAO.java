package seng202.team0.repository;

import org.apache.commons.lang3.NotImplementedException;
import seng202.team0.models.Favourite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Class that communicates with the favourites table in the database through SQL queries.
 * @author Zipporah Price
 * @author Morgan English
 */
public class FavouriteDAO implements DAOInterface<Favourite> {
    private final DatabaseManager databaseManager;

    /**
     * Creates a new FavouriteDAO object
     */
    public FavouriteDAO() { this.databaseManager = DatabaseManager.getInstance(); }

    /**
     * Gets all favourite routes from the database
     * @return List of Favourite objects
     */
    @Override
    public List<Favourite> getAll() {
        List<Favourite> favourites = new ArrayList<Favourite>();
        String sql = "SELECT * FROM favourites";
        try (Connection conn = databaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                favourites.add(new Favourite(
                        rs.getString("start_address"),
                        rs.getString("end_address"),
                        rs.getFloat("start_lat"),
                        rs.getFloat("start_lng"),
                        rs.getFloat("end_lat"),
                        rs.getFloat("end_lng"),
                        rs.getString("filters")));
            }
            return favourites;
        } catch (SQLException e) {
            System.out.println(e);
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
        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    favourite = new Favourite(
                            rs.getString("start_address"),
                            rs.getString("end_address"),
                            rs.getFloat("start_lat"),
                            rs.getFloat("start_lng"),
                            rs.getFloat("end_lat"),
                            rs.getFloat("end_lng"),
                            rs.getString("filters"));
                }
                return favourite;
            }
        } catch (SQLException sqlException) {
            System.out.println(sqlException);
            return null;
        }
    }

    /**
     * Adds a given Favourite object to a prepared statement
     * @param ps PreparedStatement being added to
     * @param toAdd Favourite object to be added
     * @throws SQLException
     */
    public void addToPreparedStatement(PreparedStatement ps, Favourite toAdd) throws SQLException {
        ps.setString(1, toAdd.getStartAddress());
        ps.setString(2, toAdd.getEndAddress());
        ps.setDouble(3, toAdd.getStartLat());
        ps.setDouble(4, toAdd.getStartLong());
        ps.setDouble(5, toAdd.getEndLat());
        ps.setDouble(6, toAdd.getEndLong());
        ps.setString(7, toAdd.getFilters());
    }

    /**
     * Adds a single given Favourite to the database
     * @param toAdd Favourite object to be added
     * @throws SQLException
     */
    @Override
    public void addOne(Favourite toAdd) throws SQLException {
        String sql = "INSERT INTO favourites (start_address, end_address, start_lat, start_lng," +
                "end_lat, end_lng, filters) values (?,?,?,?,?,?,?);";
        Connection conn = databaseManager.connect();
        PreparedStatement ps = conn.prepareStatement(sql);
        addToPreparedStatement(ps, toAdd);
        ps.executeUpdate();
    }

    /**
     * Adds a given list of Favourite objects to the database
     * @param toAdd List of Favourites to be added
     * @throws SQLException
     */
    public void addMultiple(List<Favourite> toAdd) throws SQLException {
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
            System.out.println(sqlException);
        }
    }

    /**
     * Updates a given Favourite within the database
     * @param toUpdate Favourite object to be updated
     */
    @Override
    public void update(Favourite toUpdate) {
        throw new NotImplementedException();
    }

}
