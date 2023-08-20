package seng202.team0.repository;

import org.apache.commons.lang3.NotImplementedException;
import seng202.team0.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete class communicating with user table through SQL queries
 * @author Morgan English
 * @author Angelica Silva
 */
public class UserDAO implements DAOInterface<User> {
    private final DatabaseManager databaseManager;

    private final Connection connection;

    private List<User> tempUsers = new ArrayList<>(); //just temporary list to get an idea of things
    // TODO delete tempUsers after database implemented and this class is refactored.

    /**
     * Creates a new userDAO object
     */
    public UserDAO() {
        databaseManager = new DatabaseManager();
        connection = databaseManager.getConnection();
    }

    /**
     * Gets all users in a database
     * @return a List of all users
     */
    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sqlCommand = "SELECT * FROM Users"; //Users will be a table in database
        // TODO implement with SQL database
        return users;
    }

    /**
     * Gets a single user from database based on user id
     * @param id relevant user's id
     * @return the user from the database with the corresponding id
     */
    @Override
    public User getOne(int id) {
        // TODO implement for SQL database
        String sqlCommand = "Select * FROM Users WHERE id = ?"; //'?' means placeholder for parametrized queries
        return tempUsers.get(id);
    }

    /**
     * Adds a single user to the database
     * @param userToAdd user we want to add
     * @throws SQLException provides info on database access / other errors
     */
    @Override
    public void addOne(User userToAdd) throws SQLException { //TODO create new DuplicateEntryException
        String sqlCommand = "INSERT INTO Users (username, password) VALUES (?, ?)";

        PreparedStatement ps = connection.prepareStatement(sqlCommand);
        ps.setString(1, userToAdd.getUsername());
        ps.setString(2, userToAdd.getPassword());
        ps.executeUpdate();
        //TODO add try/catch for adding to database
        //potentially return int to show error or not

    }

    //do we actually need an addMultiple function for users?

    /**
     * Deletes the user with the given userID from the database
     * @param userId the id of the user to be deleted
     * @throws SQLException provides info on database access/other errors
     */
    @Override
    public void delete(int userId) throws SQLException {
        String sqlCommand = "DELETE FROM Users WHERE id = ?";

        PreparedStatement ps = connection.prepareStatement(sqlCommand);
        ps.setInt(1, userId);
        ps.executeUpdate();
        //TODO add try/catch for deleting from database

    }

    /**
     * Updates the user in the database
     * @param userToUpdate user we want updated
     */
    @Override
    public void update(User userToUpdate) {
        throw new NotImplementedException();
    }

    /**
     * Gets a single user from database based on username
     * @param username username to filter by
     * @return the user with the matching username
     */
    public User getFromUsername(String username) { //not overridden bec. not going to be from DAO interface
        throw new NotImplementedException();
        //TODO implement and add NotFoundException
        //think about uniqueness of usernames and how to implement that
    }
}
