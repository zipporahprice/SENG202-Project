package seng202.team0.repository;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    public static void createNewDatabase(String fileName) {
        // TODO generalise where the database is made to where the jar file is
        String url = "jdbc:sqlite:" + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void initialiseDatabase(Connection databaseConnection) {
        try {
            InputStream in = getClass().getResourceAsStream("/sql/initialise_database.sql");

        }
    }

    public static void main(String[] args) {
        createNewDatabase("test.db");
    }

}
