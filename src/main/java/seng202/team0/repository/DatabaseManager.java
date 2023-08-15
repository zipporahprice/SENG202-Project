package seng202.team0.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

public class DatabaseManager {

    // TODO think about flow with jar file since the database initialiser does not work when the database exists
    // TODO which is weird because the drop and create statements should work with or without a db file
    public static void createNewDatabase(String fileName) {
        // TODO generalise where the database is made to where the jar file is
        String url = "jdbc:sqlite:" + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
                initialiseDatabase(conn);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void initialiseDatabase(Connection databaseConnection) {
        try {
            InputStream inputStream = DatabaseManager.class.getResourceAsStream("/sql/initialise_database.sql");

            // Make sure that the input stream you get null
            assert inputStream != null;
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String temp;
            StringBuilder strings = new StringBuilder();

            while ((temp=bufferedReader.readLine()) != null) {
                strings.append(temp);
            }

            String[] statements = strings.toString().split("--SPLIT");

            Statement statementConnection = databaseConnection.createStatement();
            for (String statement: statements) {
                // TODO remove manual testing to see that strings do parse correctly
                System.out.println(statement);

                // TODO
                statementConnection.execute(statement);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        createNewDatabase("test.db");

    }

}
