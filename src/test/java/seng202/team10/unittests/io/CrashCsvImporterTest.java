package seng202.team10.unittests.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.net.URL;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team10.cucumber.ViewCrashDataStepDefinitions;
import seng202.team10.exceptions.DataImportException;
import seng202.team10.io.CrashCsvImporter;
import seng202.team10.models.Crash;


/**
 * Testing CrashCSVImporter class.
 *
 * @author Neil Alombro
 *
 */

public class CrashCsvImporterTest {

    private static CrashCsvImporter testImporter;

    private static final Logger log = LogManager.getLogger(CrashCsvImporterTest.class);

    @BeforeEach
    void initialiseImporter() {
        testImporter = new CrashCsvImporter();
    }

    @Test
    void testCrashListFromFile() {

        try {
            URL url = Thread.currentThread().getContextClassLoader()
                    .getResource("files/random_5_crashes.csv");
            File file = new File(url.getPath());
            List<Crash> crashes = testImporter.crashListFromFile(file);
            assertEquals(crashes.size(), 5);
        } catch (DataImportException dataImportException) {
            log.error(dataImportException);
        }

    }

    @AfterAll
    static void tearDown() {
        testImporter = null;
    }
}
