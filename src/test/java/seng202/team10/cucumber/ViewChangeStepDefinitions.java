package seng202.team10.cucumber;

import static org.junit.Assert.assertEquals;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import seng202.team10.business.SettingsManager;



/**
 * This class defines step definitions for testing view changes in the app.
 */
public class ViewChangeStepDefinitions {

    private SettingsManager settingsManager;

    /**
     * Initializes the settings manager when the app is opened and the database is loaded.
     */
    @Given("the user has opened the app and the database is loaded")
    public void initialSettings() {
        settingsManager = SettingsManager.getInstance();
    }

    /**
     * Changes the current view to the specified view.
     *
     * @param newView The new view to be set.
     */
    @When("the user changes the view to {string}")
    public void changeView(String newView) {
        settingsManager.setCurrentView(newView);
    }

    /**
     * Verifies that the current view matches the expected view.
     *
     * @param expectedView The expected view.
     */
    @Then("the current view should be {string}")
    public void currentViewShouldBe(String expectedView) {
        String currentView = settingsManager.getCurrentView();
        assertEquals(expectedView, currentView);
    }
}

