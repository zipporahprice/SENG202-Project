package seng202.team0.business;

import seng202.team0.io.CrashCsvImporter;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class IOManager {
    private static IOManager ioManager;

    private IOManager() {

    }

    public static IOManager getInstance() {
        if (ioManager == null) {
            ioManager = new IOManager();
        }
        return ioManager;
    }

    public void importFile(File file) {
        CrashManager manager = new CrashManager();
        CrashCsvImporter importer = new CrashCsvImporter();
        manager.addAllCrashesFromFile(importer, file);
    }

}
