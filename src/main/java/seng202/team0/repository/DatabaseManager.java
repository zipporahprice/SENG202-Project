package seng202.team0.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;

/**
 *
 * Class instantiating and initialising SQLite database.
 * Majority of code taken from Morgan English's JavaFX Sales app source code.
 *
 * @author Morgan English
 * @author Angelica Silva
 * @author Christopher Wareing
 * @author Neil Alombro
 * @authod Todd Vermeir
 * @author William Thompson
 * @author Zipporah Price
 *
 */

public class DatabaseManager {
    private static final Logger log = LogManager.getLogger(DatabaseManager.class);
    private static DatabaseManager manager = null;
    private final String url;
    /**
     * Private constructor for singleton purposes
     * Creates database if it does not already exist in specified location
     * @param url Location of the db file or null
     */
    public DatabaseManager(String url) {
        // Uses url or helper function to get the relative database path
        if (url==null || url.isEmpty()) {
            this.url = this.getDatabasePath();
        } else {
            this.url = url;
        }

        // If database does not exist, create database
        if (!checkDatabaseExists(this.url)) {
            createNewDatabase(this.url);
            resetDB();
        }
    }

    /**
     * Singleton method to get current Instance if exists otherwise create it
     * @return the single instance DatabaseSingleton
     */
    public static DatabaseManager getInstance() {
        if(manager == null)
            // todo find a way to actually get db within jar
            // The following line can be used to reach a db file within the jar, however this will not be modifiable
            // instance = new DatabaseManager("jdbc:sqlite:./src/main/resources/database.db");
            manager = new DatabaseManager(null);

        return manager;
    }

    // TODO copy in initialiseInstanceWithUrl, did not copy as did not see a need for function

    /**
     *  WARNING Sets the current singleton instance to null
     */
    public static void REMOVE_INSTANCE() { manager = null; }

    /**
     * Connect to the database
     * @return database connection
     */
    public Connection connect(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(this.url);
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
        return conn;
    }

    /**
     * Initialises the database if it does not exist using the sql script included in resources
     */
    public void resetDB() {
        try {
            InputStream in = getClass().getResourceAsStream("/sql/initialise_database.sql");
            executeSQLScript(in);
        } catch (NullPointerException nullPointerException) {
            log.error(nullPointerException);
        }
    }

    /**
     * Gets path to the database relative to the jar file
     * @return jdbc encoded url location of database
     */
    private String getDatabasePath() {
        String path = DatabaseManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        File jarDir = new File(path);
        return "jdbc:sqlite:"+jarDir.getParentFile()+"/database.db";
    }

    /**
     * Check that a database exists in the expected location
     * @param url expected location to check for database
     * @return True if database exists else false
     */
    private boolean checkDatabaseExists(String url){
        File f = new File(url.substring(12));
        return f.exists();
    }

    /**
     * Creates new SQLite database file with given fileName and initialises with SQL script
     * @param url url to create database at
     */
    private void createNewDatabase(String url) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException sqlException) {
            log.error(sqlException);
        }
    }

    /**
     * Initialises given databaseConnection with SQL script creating the crashes, favourites, and users tables.
     * @param sqlFile input stream of file containing sql statements for execution (separated by --SPLIT)
     */
    private void executeSQLScript(InputStream sqlFile) {
        try {
            // Setting up reader with input stream
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sqlFile));

            // Initialising temp string and string builder
            String temp;
            StringBuilder strings = new StringBuilder();

            // Read through stream, add each string, until end signified with null
            while ((temp=bufferedReader.readLine()) != null) {
                strings.append(temp);
            }

            // List of strings that represent each statement
            String[] statements = strings.toString().split("--SPLIT");

            // Execute statements on tables
            Connection conn = this.connect();
            Statement statementConnection = conn.createStatement();
            for (String statement: statements) {
                statementConnection.execute(statement);
            }
        } catch (SQLException | IOException e) {
            log.error(e);
        }
    }
}
