package seng202.team10.unittests.repository;

import java.io.File;
import java.net.URL;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team10.exceptions.DataImportException;
import seng202.team10.io.CrashCsvImporter;
import seng202.team10.models.Crash;
import seng202.team10.repository.DatabaseManager;
import seng202.team10.repository.SqliteQueryBuilder;
import seng202.team10.unittests.io.CrashCsvImporterTest;

/**
 * Test class for DatabaseManager class.
 *
 * @author Neil Alombro
 * @author Zipporah Price
 * @author Angelica Silva
 */

public class DatabaseManagerTest {

    private static DatabaseManager manager;

    private static final Logger log = LogManager.getLogger(DatabaseManagerTest.class);

    /**
     * Initialise manager by getting instance.
     */
    @BeforeEach
    void initialiseManager() {
        manager = DatabaseManager.getInstance();
        manager.resetDb();
    }

    /**
     * Tests the database gets initialised to the correct size.
     */
    @Test
    void testInitialiseDatabase() {
        manager.initialiseDatabase("files/random_5_crashes.csv");
        List<?> crashesFromDatabase = SqliteQueryBuilder.create().
                select("*").from("crashes").buildGetter();
        Assertions.assertEquals(5, crashesFromDatabase.size());
    }

    /**
     * Tests the database gets created.
     */
    @Test
    void testCreateDatabase() {
        manager.createNewDatabase(manager.getDatabasePath());
        Assertions.assertTrue(manager.checkDatabaseExists(manager.getDatabasePath()));
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

        try {
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
        } catch (DataImportException dataImportException) {
            log.error(dataImportException);
        }
    }

}
