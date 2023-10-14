package seng202.team10.unittests.repository;

import java.io.File;
import java.net.URL;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team10.io.CrashCsvImporter;
import seng202.team10.models.Crash;
import seng202.team10.repository.DatabaseManager;
import seng202.team10.repository.SqliteQueryBuilder;

/**
 * Test class for DatabaseManager class.
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
     * Test getInstance function.
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
     * Test resetDb function.
     */
    @Test
    void testResetDb() {
        // Add crashes to database
        CrashCsvImporter importer = new CrashCsvImporter();
        URL newUrl = Thread.currentThread().getContextClassLoader()
                .getResource("files/random_5_crashes.csv");
        File testFile = new File(newUrl.getPath());
        List<Crash> crashes = importer.crashListFromFile(testFile);
        SqliteQueryBuilder.create().insert("crashes").buildSetter(crashes);

        // See if it was successful in adding the crashes to be able to see
        // if removal is successful with reset
        Assertions.assertTrue(SqliteQueryBuilder.create().select("*")
                .from("crashes").buildGetter().size() > 0);

        // Reset database
        manager.resetDb();

        // Look into database and make sure crashes table are empty
        Assertions.assertTrue(SqliteQueryBuilder.create().select("*")
                .from("crashes").buildGetter().size() == 0);
    }

}
