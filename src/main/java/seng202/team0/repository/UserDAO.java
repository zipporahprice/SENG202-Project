package seng202.team0.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.models.Favourite;
import seng202.team0.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that communicates with the users table in the database through SQL queries.
 *
 * @author Morgan English
 * @author Angelica Silva
 *
 */
public class UserDAO implements DAOInterface<User> {
    private static final Logger log = LogManager.getLogger(UserDAO.class);
    private final DatabaseManager databaseManager;

    /**
     * Creates a new userDAO object
     */
    public UserDAO() { this.databaseManager = DatabaseManager.getInstance(); }

    /**
     * Gets all users from the database
     * @return List of all users
     */
    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sqlCommand = "SELECT * FROM users"; // Users will be a table in database
        try (Connection conn = databaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlCommand)) {
            while (rs.next()) {
                User user = userFromResultSet(rs);
                assert user != null;
                users.add(user);
            }
            return users;
        } catch (AssertionError | SQLException e) {
            log.error(e);
            return new ArrayList<>();
        }
    }

    /**
     * Gets a single user from database based on user id
     * @param id relevant user's id
     * @return the user from the database with the corresponding id
     */
    @Override
    public User getOne(int id) {
        // TODO implement for SQL database
        String sqlCommand = "Select * FROM users WHERE id = ?"; //'?' means placeholder for parametrized queries

        // TODO implement this function
        log.error(new NotImplementedException());
        return null;
    }

    /**
     * Adds a single user to the database
     * @param userToAdd user we want to add
     */
    @Override
    public void addOne(User userToAdd) { //TODO create new DuplicateEntryException
        String sqlCommand = "INSERT INTO Users (username, password) VALUES (?, ?)";

        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sqlCommand);){
            ps.setString(1, userToAdd.getUsername());
            ps.setString(2, userToAdd.getPassword());
            ps.executeUpdate();
            //TODO add try/catch for adding to database
            // potentially return int to show error or not
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }

        // TODO implement this function
        log.error(new NotImplementedException());
    }

    // TODO do we actually need an addMultiple function for users?
    /**
     * Deletes the user with the given userID from the database
     * @param userId the id of the user to be deletedx
     */
    @Override
    public void delete(int userId) {
        String sqlCommand = "DELETE FROM Users WHERE id = ?";
        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sqlCommand);) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }

        // TODO implement this function
        log.error(new NotImplementedException());
    }

    /**
     * Updates the user in the database
     * @param userToUpdate user we want updated
     */
    @Override
    public void update(User userToUpdate) {
        // TODO implement this function
        log.error(new NotImplementedException());
    }

    /**
     * Gets a single user from database based on username
     * @param username username to filter by
     * @return the user with the matching username
     */
    public User getFromUsername(String username) { //not overridden bec. not going to be from DAO interface
        //TODO add NotFoundException
        //think about uniqueness of usernames and how to implement that

        // TODO implement this function
        log.error(new NotImplementedException());
        return null;
    }

    /**
     * Takes a ResultSet object and creates a User object
     * from the data of the row with corresponding column names.
     * @param rs ResultSet from executing SQL query
     * @return User object with the current row result set is at
     */
    private User userFromResultSet(ResultSet rs) {
        try {
            return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"));
        } catch (SQLException sqlException) {
            log.error(sqlException);
            return null;
        }
    }

}
