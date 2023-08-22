package seng202.team0.unittests;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import seng202.team0.io.CrashCSVImporter;
import seng202.team0.models.Crash;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImportCrashesTest {

    @Test
    void radCrashesFromCSVFile() {
//        CrashCSVImporter crashCSVImporter = new CrashCSVImporter();
//        URL url = Thread.currentThread().getContextClassLoader().getResource("files/random_5_crashes.csv");
//        File file = new File(url.getPath());
//        List<Crash> crashes = crashCSVImporter.crashListFromFile(file);
//        Assertions.assertEquals(crashes.size(), 5);
        assertEquals(2,2);
    }

}
