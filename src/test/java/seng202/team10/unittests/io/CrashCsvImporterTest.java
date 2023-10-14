package seng202.team10.unittests.io;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team10.io.CrashCsvImporter;
import seng202.team10.models.Crash;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for CrashCSVImporter class
 *
 * @author Neil Alombro
 *
 */

public class CrashCsvImporterTest {

    private static CrashCsvImporter testImporter;

    @BeforeEach
    void initialiseImporter() {
        testImporter = new CrashCsvImporter();
    }

    @Test
    void testCrashListFromFile() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("files/random_5_crashes.csv");
        File file = new File(url.getPath());
        List<Crash> crashes = testImporter.crashListFromFile(file);
        assertEquals(crashes.size(),5);
    }

    @AfterAll
    static void tearDown() {
        testImporter = null;
    }
}
