package seng202.team0;

import seng202.team0.gui.MainWindow;
import seng202.team0.repository.DatabaseManager;

/**
 * Default entry point class.
 *
 * @author seng202 teaching team
 */
public class App {

    /**
     * Entry point which runs the javaFX application.
     * Also shows off some different logging levels.
     *
     * @param args program arguments from command line
     */
    public static void main(String[] args) {
        // Initialises database and checks if populated
        DatabaseManager database = new DatabaseManager(null);
        CrashManager manager = new CrashManager();
        List crashes = manager.getCrashLocations();
        if (crashes.size() == 0) {
            try {
                CrashCsvImporter importer = new CrashCsvImporter();
                // TODO replace with full file
                InputStream stream = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("files/crash_data_10k.csv");
                File tempFile = File.createTempFile("tempCSV", ".csv");
                Files.copy(stream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                manager.addAllCrashesFromFile(importer, tempFile);
            } catch (IOException e) {
                log.error(e);
            }
        }
        // Initialises GUI
        MainWindow.main(args);

        DatabaseManager.getInstance().initialiseDatabase();



    }
}
