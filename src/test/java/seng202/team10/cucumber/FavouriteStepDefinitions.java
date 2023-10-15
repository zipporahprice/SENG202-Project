package seng202.team10.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FavouriteStepDefinitions {

    String where;

    @Given("the user has a start and end location entered on the routing menu")
    public void locations_entered() {


    }

    @When("the user clicks save route and enters a unique {string}")
    public void named_route(String name) {

    }

    @Then("the {string} route is saved in the database")
    public void route_saved(String name) {

    }

}
