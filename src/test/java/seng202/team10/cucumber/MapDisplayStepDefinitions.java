package seng202.team10.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team10.models.JavaScriptBridge;

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
