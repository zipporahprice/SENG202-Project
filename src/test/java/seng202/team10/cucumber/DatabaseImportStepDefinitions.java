package seng202.team10.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.File;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team10.exceptions.DataImportException;
import seng202.team10.repository.DatabaseManager;
import seng202.team10.repository.SqliteQueryBuilder;



/**
 * This class defines step definitions for testing the CSV data
 * import functionality of a database manager.
 */
public class DatabaseImportStepDefinitions {

    private DatabaseManager databaseManager;
    private File csvFile;
    private int expectedFileSize;
    private static final Logger log = LogManager.getLogger(DatabaseImportStepDefinitions.class);

    /**
     * Sets up the test scenario by specifying the expected file size and loading a CSV file.
     */
    @Given("the user has a CSV data file saved on the device running the app")
    public void userCsvFileSaved() {
        expectedFileSize = 5;
        csvFile = new File(getClass().getResource("/files/random_5_crashes.csv").getFile());
        log.info(csvFile.getName());
    }

    /**
     * Executes the import operation for the CSV file using the database manager.
     */
    @When("the user imports the CSV file")
    public void theUserImportsCsvFile() {
        try {
            databaseManager = DatabaseManager.getInstance();
            databaseManager.importFile(csvFile);
        } catch (DataImportException e) {
            log.error(e);
        }
    }

    /**
     * Checks if the database has been populated with data from the CSV file.
     */
    @Then("the database should be populated with data from the CSV file")
    public void databaseShouldBePopulated() {
        int isDatabasePopulated = checkIfDatabaseIsPopulated();
    }

    /**
     * Checks if the "crashes" table in the database has been populated.
     *
     * @return The number of records in the "crashes" table.
     */
    private int checkIfDatabaseIsPopulated() {
        // Establish a database connection
        List<?> crashes = SqliteQueryBuilder.create().select("*").from("crashes").buildGetter();
        return crashes.size();
    }
}
