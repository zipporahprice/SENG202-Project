package seng202.team10.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import seng202.team10.business.RouteManager;
import seng202.team10.repository.DatabaseManager;

public class FavouriteStepDefinitions {
    private String name;
    private String startLocation;
    private String endLocation;

    private RouteManager routeManager = RouteManager.getInstance();


    @Given("the user has a {string} and {string} location entered on the routing menu")
    public void locationsEntered(String startLocation, String endLocation) {
        // Make sure the database is reset and initialised
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.resetDb();
        databaseManager.initialiseDatabase("files/crash_data_10k.csv");

        // Set location strings
        routeManager.setStartLocation(startLocation);
        routeManager.setEndLocation(endLocation);
    }

    @Given("there is a route saved called {string}")
    public void favouriteExists() {
        // Make sure the database is reset and initialised
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.resetDb();
        databaseManager.initialiseDatabase("files/crash_data_10k.csv");

    }


    @When("the user clicks save route and enters a unique {string}")
    public void namedRoute(String name) {

    }

    @When("the user selects route {string} and clicks load route")
    public void routeLoadAction(String name) {

    }

    @When("the user selects route {string} and clicks delete route")
    public void routeDeleteAction(String name) {

    }


    @Then("the {string} route is saved in the database")
    public void routeSaved(String name) {

    }

    @Then("the location {string} in the route menu is matches the favourite in the database")
    public void routeLoaded(String name) {

    }

    @Then("the route {string} is deleted from the database")
    public void routeDeleted(String name) {

    }

}
