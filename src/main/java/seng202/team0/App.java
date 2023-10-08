package seng202.team0;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.business.CrashManager;
import seng202.team0.business.FilterManager;
import seng202.team0.business.RouteManager;
import seng202.team0.business.SettingsManager;
import seng202.team0.gui.MainWindow;
import seng202.team0.io.CrashCSVImporter;
import seng202.team0.repository.DatabaseManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Default entry point class
 * @author seng202 teaching team
 */
public class App {

    private static final Logger log = LogManager.getLogger(App.class);

    /**
     * Entry point which runs the javaFX application
     * Also shows off some different logging levels
     * @param args program arguments from command line
     */
    public static void main(String[] args) {
        // Initialises database and checks if populated
        DatabaseManager database = new DatabaseManager(null);
        CrashManager manager = new CrashManager();
        List crashes = manager.getCrashLocations();
        if (crashes.size() == 0) {
            try {
                CrashCSVImporter importer = new CrashCSVImporter();
                // TODO replace with full file
                InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("files/crash_data.csv");
                File tempFile = File.createTempFile("tempCSV", ".csv");
                Files.copy(stream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                manager.addAllCrashesFromFile(importer, tempFile);
            } catch (IOException e) {
                log.error(e);
            }
        }

        // Initialise FilterManager singleton class
        FilterManager.getInstance();

        // Initialise RouteManager singleton class
        RouteManager.getInstance();

        // Initialise SettingsManager singleton class
        SettingsManager.getInstance();

        // Initialises GUI
        MainWindow.main(args);
    }
}
