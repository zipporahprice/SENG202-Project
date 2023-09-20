package seng202.team0.unittests.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.io.CrashCSVImporter;
import seng202.team0.models.Crash;
import seng202.team0.repository.CrashDAO;
import seng202.team0.repository.DatabaseManager;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Test class for DatabaseManager class
 *
 * @author Neil Alombro
 *
 */

public class DatabaseManagerTest {

    private static DatabaseManager manager;

    /**
     * Initialise manager by getting instance.
     */
    @BeforeEach
    void initialiseManager() {
        manager = DatabaseManager.getInstance();
    }

    /**
     * Test getInstance function
     */
    @Test
    void testGetInstance() {
        Assertions.assertNotNull(DatabaseManager.getInstance());
    }

    /**
     * Test connect function.
     */
    @Test
    void testConnect() {
        Assertions.assertNotNull(manager.connect());
    }

    /**
     * Test resetDB function.
     */
    @Test
    void testResetDB() {
        // Add crashes to database
        CrashDAO crashDAO = new CrashDAO();
        CrashCSVImporter importer = new CrashCSVImporter();
        URL newUrl = Thread.currentThread().getContextClassLoader().getResource("files/random_5_crashes.csv");
        File testFile = new File(newUrl.getPath());
        List<Crash> crashes = importer.crashListFromFile(testFile);
        crashDAO.addMultiple(crashes);

        // See if it was successful in adding the crashes to be able to see
        // if removal is successful with reset
        Assertions.assertTrue(crashDAO.getAll().size() > 0);

        // Reset database
        manager.resetDB();

        // Look into database and make sure crashes table are empty
        Assertions.assertTrue(crashDAO.getAll().size() == 0);
    }
}
