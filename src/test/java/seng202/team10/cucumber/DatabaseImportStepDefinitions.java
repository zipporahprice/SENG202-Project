package seng202.team10.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team10.exceptions.DataImportException;
import seng202.team10.repository.DatabaseManager;
import seng202.team10.repository.SqliteQueryBuilder;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DatabaseImportStepDefinitions {

    private DatabaseManager databaseManager;
    private File csvFile;
    private int expectedFileSize;
    private static final Logger log = LogManager.getLogger(DatabaseImportStepDefinitions.class);

    @Given("the user has a CSV data file saved on the device running the app")
    public void userCsvFileSaved() {
        expectedFileSize = 5;
        csvFile = new File(getClass().getResource("/files/random_5_crashes.csv").getFile());
        System.out.println(csvFile.getName());

    }

    @When("the user imports the CSV file")
    public void theUserImportsCsvFile() {
        try {
            databaseManager = DatabaseManager.getInstance();
            databaseManager.importFile(csvFile);
        } catch (DataImportException e) {
            log.error(e);
        }
    }

    @Then("the database should be populated with data from the CSV file")
    public void databaseShouldBePopulated() {
        int isDatabasePopulated = checkIfDatabaseIsPopulated();
//        assertEquals(expectedFileSize, isDatabasePopulated); //need to look into this
    }


    private int checkIfDatabaseIsPopulated() {
        // Establish a database connection
        List<?> crashes = SqliteQueryBuilder.create().select("*").from("crashes").buildGetter();
        return crashes.size();
    }
}
