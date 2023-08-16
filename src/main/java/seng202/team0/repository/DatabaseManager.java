package seng202.team0.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

/**
 * Class instantiating and initialising SQLite database.
 * @author Neil Alombro
 */

public class DatabaseManager {

    // TODO think about flow with jar file since the database initializer does not work when the database exists
    // TODO which is weird because the drop and create statements should work with or without a db file

    private final Connection connection;

    public DatabaseManager() {
        // TODO change this, currently manually testing
        this.connection = createNewDatabase("test.db");
    }

    /**
     * Creates new SQLite database file with given fileName and initialises with SQL script
     * @param fileName file name
     */
    public static Connection createNewDatabase(String fileName) {
        // TODO generalise where the database is made to where the jar file is
        String url = "jdbc:sqlite:" + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
                initialiseDatabase(conn);
                return conn;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Initialises given databaseConnection with SQL script creating the crashes, favourites, and users tables.
     * @param databaseConnection database connection
     */
    public static void initialiseDatabase(Connection databaseConnection) {
        try {
            // Make sure that the input stream you get is not null
            InputStream inputStream = DatabaseManager.class.getResourceAsStream("/sql/initialise_database.sql");
            assert inputStream != null;

            // Setting up reader with input stream
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

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
            Statement statementConnection = databaseConnection.createStatement();
            for (String statement: statements) {
                // TODO remove manual testing to see that strings do parse correctly
//                System.out.println(statement);

                statementConnection.execute(statement);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return this.connection;
    }
}
