package seng202.team10;

import seng202.team10.gui.MainWindow;
import seng202.team10.repository.DatabaseManager;

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
        DatabaseManager.getInstance().initialiseDatabase("files/crash_data.csv");

        MainWindow.main(args);
    }
}
