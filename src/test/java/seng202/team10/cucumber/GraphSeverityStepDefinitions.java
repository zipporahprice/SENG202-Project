package seng202.team10.cucumber;

import static org.junit.Assert.assertEquals;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import seng202.team10.business.GraphManager;


/**
 * This class defines step definitions for testing
 * graph-related functionality, specifically, severity filters.
 */
public class GraphSeverityStepDefinitions {

    private GraphManager graphManager;

    /**
     * Initializes the graph manager when the app is opened, and the pie chart is accessed.
     */
    @Given("the user has opened the app and has opened the pie chart")
    public void openPieChart() {
        graphManager = GraphManager.getInstance();
    }

    /**
     * Changes the filter to a specified severity level.
     *
     * @param newData The new severity level filter to be applied.
     */
    @When("the user changes the filter to {string}")
    public void selectSeverity(String newData) {
        graphManager.setCurrentColumnData(newData);
    }

    /**
     * Verifies that the graph displays data from the expected severity column.
     *
     * @param expectedCol The expected severity column.
     */
    @Then("the graph updates to show data from {string}")
    public void seeData(String expectedCol) {
        String currentCol = graphManager.getCurrentColumnData();
        assertEquals(expectedCol, currentCol);
    }
}

