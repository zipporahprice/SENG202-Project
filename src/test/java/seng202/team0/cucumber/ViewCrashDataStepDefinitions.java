package seng202.team0.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team0.io.CrashCsvImporter;
import seng202.team0.models.Crash;
import seng202.team0.repository.CrashDao;
import seng202.team0.repository.DatabaseManager;
import seng202.team0.repository.SqliteQueryBuilder;

import java.io.File;
import java.net.URL;
import java.util.List;

public class ViewCrashDataStepDefinitions {
    Crash crashSelected = null;

    @Given("the user wants to see crash information")
    public void selectCrash() {
        // TODO figure out how to do the visual-based acceptance tests

        // Database setup
        DatabaseManager.getInstance().resetDb();
        CrashDao crashDAO = new CrashDao();
        CrashCsvImporter importer = new CrashCsvImporter();
        URL newUrl = Thread.currentThread().getContextClassLoader().getResource("files/random_5_crashes.csv");
        File testFile = new File(newUrl.getPath());
        List<Crash> crashes = importer.crashListFromFile(testFile);
        crashDAO.addMultiple(crashes);
    }
    @When("The user selects crash")
    public void userSelectsCrash() {
        CrashDao crashDao = new CrashDao();
        crashSelected = crashDao.getAll().get(0);
    }

    @Then("The user will see information on the crash")
    public void informationOfCrash() {
        double range = 0.00001;

        double minLat = crashSelected.getLatitude() - range;
        double maxLat = crashSelected.getLatitude() + range;
        double minLng = crashSelected.getLongitude() - range;
        double maxLng = crashSelected.getLongitude() + range;

        String whereClause = "latitude BETWEEN " + minLat + " AND " + maxLat +
                " AND longitude BETWEEN " + minLng + " AND " + maxLng;

        List crashesFromDatabase = SqliteQueryBuilder.create()
                .select("object_id").from("crashes")
                .where(whereClause).buildGetter();

        Assertions.assertTrue(crashesFromDatabase.size() > 0);
    }
}
