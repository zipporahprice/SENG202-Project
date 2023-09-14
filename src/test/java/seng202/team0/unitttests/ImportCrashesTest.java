package seng202.team0.unitttests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team0.io.CrashCSVImporter;
import seng202.team0.models.Crash;

import java.io.File;
import java.net.URL;
import java.util.List;

public class ImportCrashesTest {

    @Test
    void roadCrashesFromCSVFile() {
        CrashCSVImporter crashCSVImporter = new CrashCSVImporter();
        URL url = Thread.currentThread().getContextClassLoader().getResource("files/random_5_crashes.csv");
        File file = new File(url.getPath());
        List<Crash> crashes = crashCSVImporter.crashListFromFile(file);
        Assertions.assertEquals(crashes.size(), 5);
    }

}
