package seng202.team0.repository;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface for Database Access Objects (DAOs) providing common functionality for database access.
 *
 * @author Morgan English
 */
public interface DaoInterface<T> {
    /**
     * Gets all of T from the database.
     *
     * @return List of all objects type T from the database
     */
    List<T> getAll();

    /**
     * Gets a single object of type T from the database by id.
     *
     * @param id id of object to get
     * @return Object of type T that has id given
     */
    T getOne(int id);

    /**
     * Adds a single object of type T to database.
     *
     * @param toAdd object of type T to add
     */
    void addOne(T toAdd) throws SQLException;

    //        throws DuplicateEntryException;
    //TODO consider making duplicate entry exception
    // throws DuplicateEntryException if the object already exists
    //TODO consider making return type int to show error or not

    /**
     * Deletes and object from database that matches id given.
     *
     * @param id id of object to delete
     */
    void delete(int id) throws SQLException;

    /**
     * Updates an object in the database.
     *
     * @param toUpdate Object needing to be updated (it must identify itself & its previous self)
     */
    void update(T toUpdate);

}
