package seng202.team0.cucumber;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team0.io.CrashCSVImporter;
import seng202.team0.models.Crash;
import seng202.team0.models.JavaScriptBridge;
import seng202.team0.repository.CrashDAO;
import seng202.team0.repository.DatabaseManager;
import seng202.team0.repository.SQLiteQueryBuilder;

import java.io.File;
import java.net.URL;
import java.util.List;

public class ViewCrashDataStepDefinitions {
    Crash crashSelected = null;

    @Given("the user wants to see crash information")
    public void selectCrash() {
        // TODO figure out how to do the visual-based acceptance tests

        // Database setup
        DatabaseManager.getInstance().resetDB();
        CrashDAO crashDAO = new CrashDAO();
        CrashCSVImporter importer = new CrashCSVImporter();
        URL newUrl = Thread.currentThread().getContextClassLoader().getResource("files/random_5_crashes.csv");
        File testFile = new File(newUrl.getPath());
        List<Crash> crashes = importer.crashListFromFile(testFile);
        crashDAO.addMultiple(crashes);
    }
    @When("The user selects crash")
    public void userSelectsCrash() {
        JavaScriptBridge bridge = new JavaScriptBridge();
        String crashesString = bridge.crashes();
        Gson gson = new Gson();
        List<Crash> crashList = gson.fromJson(crashesString, new TypeToken<List<Crash>>() {}.getType());
        crashSelected = crashList.get(0);
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

        List crashesFromDatabase = SQLiteQueryBuilder.create()
                .select("object_id").from("crashes")
                .where(whereClause).build();

        Assertions.assertTrue(crashesFromDatabase.size() > 0);
    }
}
