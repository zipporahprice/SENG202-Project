package seng202.team0.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.business.CrashManager;
import seng202.team0.io.CrashCsvImporter;

/**
 * Class instantiating and initialising SQLite database.
 * Majority of code taken from Morgan English's JavaFX Sales app source code.
 *
 * @author Morgan English
 * @author Angelica Silva
 * @author Christopher Wareing
 * @author Neil Alombro
 * @author Todd Vermeir
 * @author William Thompson
 * @author Zipporah Price
 *
 */

public class DatabaseManager {
    private static final Logger log = LogManager.getLogger(DatabaseManager.class);
    private static DatabaseManager manager = null;
    private final String url;

    /**
     * Private constructor for singleton purposes.
     * Creates database if it does not already exist in specified location
     *
     * @param url Location of the db file or null
     */
    private DatabaseManager(String url) {
        // Uses url or helper function to get the relative database path
        if (url == null || url.isEmpty()) {
            this.url = this.getDatabasePath();
        } else {
            this.url = url;
        }

        // If database does not exist, create database
        if (!checkDatabaseExists(this.url)) {
            createNewDatabase(this.url);
            resetDb();
        }
    }

    /**
     * Singleton method to get current Instance if exists otherwise create it.
     *
     * @return the single instance DatabaseSingleton
     */
    public static DatabaseManager getInstance() {
        if (manager == null) {
            // todo find a way to actually get db within jar
            // The following line can be used to reach a db file within the jar,
            // however this will not be modifiable
            // instance = new DatabaseManager("jdbc:sqlite:./src/main/resources/database.db");
            manager = new DatabaseManager(null);
        }

        return manager;
    }

    /**
     * Initialises database and checks if populated.
     */
    public void initialiseDatabase(String fileName) {
        double start = System.currentTimeMillis();
        CrashManager manager = new CrashManager();
        List<?> crashes = manager.getCrashLocations();
        if (crashes.size() == 0) {
            try {
                InputStream stream = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream(fileName);
                File tempFile = File.createTempFile("tempCSV", ".csv");
                assert stream != null;
                Files.copy(stream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                importFile(tempFile);

                executeSqlScript(getClass().getResourceAsStream("/sql/populate_rtree.sql"));
            } catch (IOException e) {
                log.error(e);
            }
        }

        double end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    /**
     * Connect to the database.
     *
     * @return database connection
     */
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(this.url);
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
        return conn;
    }

    /**
     * Initialises the database if it does not exist using the sql script included in resources.
     */
    public void resetDb() {
        try {
            InputStream in = getClass().getResourceAsStream("/sql/initialise_database.sql");
            executeSqlScript(in);
        } catch (NullPointerException nullPointerException) {
            log.error(nullPointerException);
        }
    }

    /**
     * Gets path to the database relative to the jar file.
     *
     * @return jdbc encoded url location of database
     */
    private String getDatabasePath() {
        String path = DatabaseManager.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath();
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        File jarDir = new File(path);
        return "jdbc:sqlite:" + jarDir.getParentFile() + "/database.db";
    }

    /**
     * Check that a database exists in the expected location.
     *
     * @param url expected location to check for database
     * @return True if database exists else false
     */
    private boolean checkDatabaseExists(String url) {
        File f = new File(url.substring(12));
        return f.exists();
    }

    /**
     * Creates new SQLite database file with given fileName and initialises with SQL script.
     *
     * @param url url to create database at
     */
    private void createNewDatabase(String url) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    /**
     * Initialises given databaseConnection with SQL script
     * creating the crashes, favourites, and users tables.
     *
     * @param sqlFile input stream of file containing
     *                sql statements for execution (separated by --SPLIT)
     */
    private void executeSqlScript(InputStream sqlFile) {
        try (Connection conn = this.connect();
             Statement statementConnection = conn.createStatement()) {
            // Setting up reader with input stream
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sqlFile));

            // Initialising temp string and string builder
            String temp;
            StringBuilder strings = new StringBuilder();

            // Read through stream, add each string, until end signified with null
            while ((temp = bufferedReader.readLine()) != null) {
                strings.append(temp);
            }

            // List of strings that represent each statement
            String[] statements = strings.toString().split("--SPLIT");

            // Execute statements on tables
            for (String statement : statements) {
                statementConnection.execute(statement);
            }
        } catch (SQLException | IOException e) {
            log.error(e);
        }
    }

    /**
     * Adds all the file data from the chosen to the database.
     *
     * @param file the file user chooses
     */
    public void importFile(File file) {
        CrashManager manager = new CrashManager();
        CrashCsvImporter importer = new CrashCsvImporter();
        manager.addAllCrashesFromFile(importer, file);
    }
}
