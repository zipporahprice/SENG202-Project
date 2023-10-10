package seng202.team0.business;

import java.io.File;
import seng202.team0.io.CrashCsvImporter;

/**
 * Class for importing.
 */
public class DataManager {
    private static DataManager dataManager;

    private DataManager() {

    }

    /**
     * gets instance of or creates a new DataManager.
     *
     * @return the dataManager
     */
    public static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    /**
     * adds all the file data from the chosen to the database.
     *
     * @param file the file user chooses
     */
    public void importFile(File file) {
        CrashManager manager = new CrashManager();
        CrashCsvImporter importer = new CrashCsvImporter();
        manager.addAllCrashesFromFile(importer, file);
    }

}
