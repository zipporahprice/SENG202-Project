package seng202.team10.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import seng202.team10.business.SettingsManager;

import static org.junit.Assert.assertEquals;

public class ViewChangeStepDefinitions {

    private SettingsManager settingsManager;

    @Given("the user has opened the app and the database is loaded")
    public void initialSettings() {
        settingsManager = SettingsManager.getInstance();
    }

    @When("the user changes the view to {string}")
    public void changeView(String newView) {
        settingsManager.setCurrentView(newView);
    }

    @Then("the current view should be {string}")
    public void currentViewShouldBe(String expectedView) {
        String currentView = settingsManager.getCurrentView();
        assertEquals(expectedView, currentView);
    }

}
