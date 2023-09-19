package seng202.team0;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.business.CrashManager;
import seng202.team0.business.FilterManager;
import seng202.team0.gui.MainWindow;
import seng202.team0.io.CrashCSVImporter;
import seng202.team0.repository.CrashDAO;
import seng202.team0.repository.DatabaseManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
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
    public static void main(String[] args) throws SQLException {
        log.info("Hello World!");
        log.warn("This is a warning message! Use this log type to 'warn' if something is not quite right");
        log.error("An error has occurred, thanks logging for helping find it! (This is a terrible error log message, but is only an example!')");
        log.log(Level.INFO, "There are many ways to log!");

        // Initialises database and checks if populated
        DatabaseManager database = new DatabaseManager(null);
        CrashManager manager = new CrashManager();
        List crashes = manager.getCrashLocations();
        if (crashes.size() == 0) {
            try {
                CrashCSVImporter importer = new CrashCSVImporter();
                // TODO replace with full file
                InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("files/crash_data_10k.csv");
                File tempFile = File.createTempFile("tempCSV", ".csv");
                Files.copy(stream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                manager.addAllCrashesFromFile(importer, tempFile);
            } catch (IOException e) {
                log.error(e);
            }
        }

        // Initialise FilterManager singleton class
        FilterManager.getInstance();

        // Initialises GUI
        MainWindow.main(args);
    }
}
