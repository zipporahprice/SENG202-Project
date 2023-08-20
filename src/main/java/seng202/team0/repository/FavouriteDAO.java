package seng202.team0.repository;

import org.apache.commons.lang3.NotImplementedException;
import seng202.team0.models.Favourite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Class that communicates with the favourites table in the database through SQL queries.
 * @author Zipporah Price
 */
public class FavouriteDAO implements DAOInterface<Favourite> {
    private final DatabaseManager databaseManager;

    public FavouriteDAO() { this.databaseManager = DatabaseManager.getInstance(); }

    @Override
    public List<Favourite> getAll() {
        List<Favourite> favouriteList = new ArrayList<Favourite>();
        return favouriteList;
    }

    @Override
    public Favourite getOne(int id) {
        throw new NotImplementedException();
    }

    public void addToPreparedStatement(PreparedStatement ps, Favourite toAdd) throws SQLException {
        ps.setInt(1, toAdd.getId());
        ps.setFloat(2, toAdd.getStartLat());
        ps.setFloat(3, toAdd.getStartLong());
        ps.setFloat(4, toAdd.getEndLat());
        ps.setFloat(5, toAdd.getEndLong());
        ps.setString(6, toAdd.getFilters());
    }

    @Override
    public void addOne(Favourite toAdd) throws SQLException {
        String sql = "INSERT INTO crashes (id, start_lat, start_long," +
                "end_lat, end_long, filters) values (?,?,?,?,?,?);";
        Connection conn = databaseManager.connect();
        PreparedStatement ps = conn.prepareStatement(sql);
        addToPreparedStatement(ps, toAdd);
        ps.executeUpdate();
    }

    public void addMultiple(List<Favourite> toAdd) throws SQLException {
        String sql = "INSERT OR IGNORE INTO crashes (id, start_lat, start_long" +
                "end_lat, end_long, filters) values (?,?,?,?,?,?);";
        Connection conn = databaseManager.connect();
        PreparedStatement ps = conn.prepareStatement(sql);
        conn.setAutoCommit(false);
        for (Favourite favToAdd : toAdd) {
            addToPreparedStatement(ps, favToAdd);
            ps.addBatch();
        }
        ps.executeBatch();
    }

    @Override
    public void delete(int objectId) {
        throw new NotImplementedException();
    }

    @Override
    public void update(Favourite toUpdate) {
        throw new NotImplementedException();
    }

}
