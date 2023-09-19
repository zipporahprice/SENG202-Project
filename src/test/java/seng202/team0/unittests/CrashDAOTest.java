package seng202.team0.unittests;


import org.junit.jupiter.api.*;
import seng202.team0.business.CrashManager;
import seng202.team0.models.Crash;
import seng202.team0.repository.CrashDAO;
import seng202.team0.io.CrashCSVImporter;

import java.io.File;
import java.net.URL;
import java.sql.*;


public class CrashDAOTest {

    private static CrashDAO testDAO;
    private static CrashCSVImporter testImporter;
    private static CrashManager testManager;
    private static File testFile;

    @BeforeEach
    void testCreate() throws SQLException {
        testDAO = new CrashDAO();
        testImporter = new CrashCSVImporter();
        testManager = new CrashManager();

        URL newUrl = Thread.currentThread().getContextClassLoader().getResource("files/random_5_crashes.csv");
        testFile = new File(newUrl.getPath());
        testManager.addAllCrashesFromFile(testImporter, testFile);
    }

    @Test
    void testGetAll() throws SQLException {
        int size = testDAO.getAll().size();
        Assertions.assertEquals(5, size);
    }

    @AfterAll
    static void tearDown() {
        testDAO = null;
        testImporter = null;
        testManager = null;
        testFile = null;
    }

}
