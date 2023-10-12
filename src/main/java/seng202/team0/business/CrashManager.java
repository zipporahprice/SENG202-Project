package seng202.team0.business;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.io.CrashCsvImporter;
import seng202.team0.models.Crash;
import seng202.team0.repository.SqliteQueryBuilder;

import java.io.File;
import java.util.List;

/**
 * Manages operations related to Crash data, including importing Crash data from CSV files,
 * retrieving individual Crash objects by ID, and retrieving filtered crash locations.
 * This class interacts with the repository layer and utilizes the FilterManager for filtering data.
 *
 * @author Angelica Silva
 * @author Christopher Wareing
 * @author Neil Alombro
 * @author Todd Vermeir
 * @author William Thompson
 * @author Zipporah Price
 *
 */

public class CrashManager {
    private static final Logger log = LogManager.getLogger(CrashManager.class);

    /**
     * Saves a file of sales to the repository layer.
     * Does this by using the specified crash csv importer functionality.
     *
     * @param importer Crash csv importer object to use
     * @param file File to be imported
     */
    public void addAllCrashesFromFile(CrashCsvImporter importer, File file) {
        List<Crash> crashes = importer.crashListFromFile(file);
        SqliteQueryBuilder.create().insert("crashes").buildSetter(crashes);
    }

    /**
     * Retrieves a Crash object by its unique identifier (ID) from the database.
     *
     * @param id The unique identifier (ID) of the Crash object to retrieve.
     * @return The Crash object associated with the specified ID, or null if no such Crash is found.
     */
    public Crash getCrash(int id) {
        return (Crash) SqliteQueryBuilder.create().select("*").from("crashes")
                .where("object_id = "+ id).buildGetter().get(0);
    }

    /**
     * Retrieves crash locations from the database based on selected filters.
     *
     * @return A list of crash locations containing longitude, latitude, and severity.
     */
    public List getCrashLocations() {
        // TODO Currently hard coding according to CrashInfo model
        // TODO Look at generalising this

        String select = "longitude, latitude, severity, crash_year, weather";
        String from = "crashes";
        String where = FilterManager.getInstance().toString();

        if (where.length() == 0) {
            return SqliteQueryBuilder
                    .create()
                    .select(select)
                    .from(from)
                    .buildGetter();
        } else {
            return SqliteQueryBuilder
                    .create()
                    .select(select)
                    .from(from)
                    .where(where)
                    .buildGetter();
        }
    }
}
