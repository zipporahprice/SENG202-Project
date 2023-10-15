package seng202.team10.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import seng202.team10.App;
import seng202.team10.exceptions.DataImportException;
import seng202.team10.io.CrashCsvImporter;
import seng202.team10.models.Crash;
import seng202.team10.repository.DatabaseManager;
import seng202.team10.repository.SqliteQueryBuilder;
import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Step definitions for Cucumber BDD testing related to viewing crash data.
 * <p>
 * Handles scenarios related to selecting, viewing, and asserting crash information,
 * utilizing CrashCsvImporter for data import, and interacting with the database
 * to validate UI operations. Also manages setup and assertions for test scenarios
 * where crash data is visualized or queried.
 *
 * @author Team 10
 */
public class ViewCrashDataStepDefinitions {

    private static final Logger log = LogManager.getLogger(ViewCrashDataStepDefinitions.class);
    Crash crashSelected = null;

    /**
     * Sets up the initial state and preconditions for viewing crash information.
     */
    @Given("the user wants to see crash information")
    public void selectCrash() {
        // TODO figure out how to do the visual-based acceptance tests

        try {
            // Database setup
            DatabaseManager.getInstance().resetDb();
            CrashCsvImporter importer = new CrashCsvImporter();
            URL newUrl = Thread.currentThread().getContextClassLoader().getResource("files/random_5_crashes.csv");
            File testFile = new File(newUrl.getPath());
            List<Crash> crashes = importer.crashListFromFile(testFile);
            SqliteQueryBuilder.create().insert("crashes").buildSetter(crashes);
        } catch (DataImportException dataImportException) {
            log.error(dataImportException);
        }
    }

    /**
     * Simulates user action of selecting a crash instance.
     */
    @When("The user selects crash")
    public void userSelectsCrash() {
        crashSelected = (Crash) SqliteQueryBuilder.create().select("*").from("crashes").buildGetter().get(0);
    }

    /**
     * Verifies that the selected crash data is displayed or otherwise accessible to the user.
     */
    @Then("The user will see information on the crash")
    public void informationOfCrash() {
        double range = 0.00001;

        double minLat = crashSelected.getLatitude() - range;
        double maxLat = crashSelected.getLatitude() + range;
        double minLng = crashSelected.getLongitude() - range;
        double maxLng = crashSelected.getLongitude() + range;

        String whereClause = "latitude BETWEEN " + minLat + " AND " + maxLat
                + "\n AND longitude BETWEEN " + minLng + " AND " + maxLng;

        List crashesFromDatabase = SqliteQueryBuilder.create()
                .select("object_id").from("crashes")
                .where(whereClause).buildGetter();

        Assertions.assertTrue(crashesFromDatabase.size() > 0);
    }
}
