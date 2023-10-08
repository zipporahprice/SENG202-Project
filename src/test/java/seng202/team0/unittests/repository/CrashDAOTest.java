package seng202.team0.unittests.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.business.CrashManager;
import seng202.team0.io.CrashCsvImporter;
import seng202.team0.repository.CrashDAO;
import seng202.team0.models.Crash;
import seng202.team0.repository.DatabaseManager;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Test class for CrashDAO class
 *
 * @author Neil Alombro
 * @author Zipporah Price
 *
 */

public class CrashDAOTest {
    private static CrashDAO testDAO;
    private static CrashCsvImporter testImporter;
    private static CrashManager testManager;
    private static File testFile;

    /**
     * Initialising DAO, importer, and manager. Adds five test crashes to the database.
     */
    @BeforeEach
    void testCreate() {
        testDAO = new CrashDAO();
        testImporter = new CrashCsvImporter();
        testManager = new CrashManager();
        DatabaseManager.getInstance().resetDB();

        URL newUrl = Thread.currentThread().getContextClassLoader().getResource("files/random_5_crashes.csv");
        testFile = new File(newUrl.getPath());
        testManager.addAllCrashesFromFile(testImporter, testFile);
    }

    /**
     * Tests getAll function.
     */
    @Test
    void testGetAll() {
        int size = testDAO.getAll().size();
        Assertions.assertEquals(5, size);
    }

    /**
     * Tests getOne function.
     */
    @Test
    void testGetOne() {
        Crash crash = testDAO.getOne(1);
        Assertions.assertEquals(crash.getObjectId(), 1);
    }

    /**
     * Tests addOne function.
     */
    @Test
    void testAddOne() {
        CrashCsvImporter importer = new CrashCsvImporter();
        List<Crash> sales = importer.crashListFromFile(testFile);
        Crash expectedCrash = sales.get(0);
        List beforeCrashes = testDAO.getAll();
        testDAO.addOne(expectedCrash);
        List afterCrashes = testDAO.getAll();

        Assertions.assertEquals(beforeCrashes.size() + 1, afterCrashes.size());
    }

    /**
     * Tests addMultiple function.
     */
    @Test
    void testAddMultiple() {
        CrashCsvImporter importer = new CrashCsvImporter();
        List<Crash> crashes = importer.crashListFromFile(testFile);
        List beforeCrashes = testDAO.getAll();
        testDAO.addMultiple(crashes);
        List afterCrashes = testDAO.getAll();

        Assertions.assertEquals(beforeCrashes.size() + crashes.size(), afterCrashes.size());
    }

    /**
     * Tearing down of DAO, importer, manager, and file.
     */
    @AfterAll
    static void tearDown() {
        testDAO = null;
        testImporter = null;
        testManager = null;
        testFile = null;
    }
}
