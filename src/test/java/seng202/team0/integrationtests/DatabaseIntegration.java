package seng202.team0.integrationtests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team0.business.CrashManager;
import seng202.team0.io.CrashCSVImporter;
import seng202.team0.repository.DatabaseManager;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;

public class DatabaseIntegration {

    @Test
    void addCrashesToDb() throws SQLException {
        DatabaseManager database = new DatabaseManager(null);
        CrashCSVImporter importer = new CrashCSVImporter();
        CrashManager manager = new CrashManager();

        URL newUrl = Thread.currentThread().getContextClassLoader().getResource("files/crash_data_10k.csv");
        File file = new File(newUrl.getPath());
        manager.addAllCrashesFromFile(importer, file);

        // File hard coded has 10,272 crashes
        Assertions.assertEquals(manager.getCrashes().size(), 10272);
    }

    @Test
    void queryCrashesTable() {
    }
}
