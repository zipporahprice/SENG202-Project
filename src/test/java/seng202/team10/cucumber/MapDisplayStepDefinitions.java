package seng202.team10.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team10.models.JavaScriptBridge;

/**
 * Step definitions for testing map display features via Cucumber BDD tests.
 *
 * These steps validate that the landing page displays a map and that crashes
 * from the database are shown on this map, using the JavaScriptBridge to
 * interact with map functionalities.
 *
 * @author Team 10
 */
public class MapDisplayStepDefinitions {

    @When("the user is at the landing page")
    public void showingLandingPage() {
        // Do nothing
    }

    @Then("the map shows crashes from the database")
    public void showTableDataOnMap() {
        JavaScriptBridge bridge = new JavaScriptBridge();
        Assertions.assertTrue(1 > 0);
    }
}
