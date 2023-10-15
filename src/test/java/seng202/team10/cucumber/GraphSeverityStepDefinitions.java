package seng202.team10.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import seng202.team10.business.GraphManager;

import static org.junit.Assert.assertEquals;

public class GraphSeverityStepDefinitions {

    private GraphManager graphManager;


    @Given("the user has opened the app and has opened the pie chart")
    public void openPieChart() {
        graphManager = GraphManager.getInstance();
    }

    @When("the user changes the filter to {string}")
    public void selectSeverity(String newData) {
        graphManager.setCurrentColumnData(newData);
    }

    @Then("the graph updates to show data from {string}")
    public void seeData(String expectedCol) {
        String currentCol = graphManager.getCurrentColumnData();
        assertEquals(expectedCol, currentCol);
    }
}
