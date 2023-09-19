package seng202.team0.business;

import seng202.team0.io.CrashCSVImporter;
import seng202.team0.models.Crash;
import seng202.team0.repository.CrashDAO;
import seng202.team0.repository.SQLiteQueryBuilder;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages operations related to Crash data, including importing Crash data from CSV files,
 * retrieving individual Crash objects by ID, and retrieving filtered crash locations.
 * This class interacts with the repository layer and utilizes the FilterManager for filtering data.
 *
 * @author Team 10
 */

public class CrashManager {

    private final CrashDAO crashDAO;

    public CrashManager() { crashDAO = new CrashDAO(); }

    /**
     * Saves a file of sales to the repository layer using the specified crash csv importer functionality
     * TODO: handle errors gracefully
     * @param importer crash csv importer object to use
     * @param file file to be imported
     */
    public void addAllCrashesFromFile(CrashCSVImporter importer, File file) throws SQLException {
        List<Crash> sales = importer.crashListFromFile(file);
        crashDAO.addMultiple(sales);
    }

    /**
     * Retrieves a Crash object by its unique identifier (ID) from the database.
     *
     * @param id The unique identifier (ID) of the Crash object to retrieve.
     * @return The Crash object associated with the specified ID, or null if no such Crash is found.
     * @throws SQLException If an error occurs while accessing the database.
     */
    public Crash getCrash(int id) throws SQLException {
        return crashDAO.getOne(id);
    }

    /**
     * Retrieves crash locations from the database based on selected filters.
     *
     * @return A list of crash locations represented as database records containing longitude, latitude, and severity.
     * @throws SQLException If an error occurs while accessing the database.
     */
    public List getCrashLocations() throws SQLException {
        // TODO do not hard code for severities, make it flexible to all filters
        // TODO could instead do a * for columns
        // TODO although * does not work for query builder weirdly

        String select = "longitude, latitude, severity";
        String from = "crashes";
        String where = FilterManager.getInstance().toString();

        if (where.length() == 0) {
            return SQLiteQueryBuilder
                    .create()
                    .select(select)
                    .from(from)
                    .build();
        } else {
            return SQLiteQueryBuilder
                    .create()
                    .select(select)
                    .from(from)
                    .where(where)
                    .build();
        }
    }
}
