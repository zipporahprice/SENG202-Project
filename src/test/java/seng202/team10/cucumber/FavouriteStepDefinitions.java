package seng202.team10.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team10.business.RouteManager;
import seng202.team10.models.Crash;
import seng202.team10.models.Favourite;
import seng202.team10.repository.DatabaseManager;
import seng202.team10.repository.SqliteQueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavouriteStepDefinitions {
    private String routeName;
    private Favourite favourite;
    private String start;
    private String end;
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
    public void favouriteExists(String routeName) {
        // Make sure the database is reset and initialised
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.resetDb();
        databaseManager.initialiseDatabase("files/crash_data_10k.csv");

        // insert a route named into database
        // TODO helpppp
        favourite = new Favourite(start, end,
                0, 0, 0,
                0, null, "car", routeName);
        SqliteQueryBuilder.create().insert("favourites").buildSetter(new ArrayList<>(Arrays.asList(favourite)));
    }


    @When("the user clicks save route and enters a unique {string}")
    public void namedRoute(String routeName) {
        this.routeName = routeName;
    }

    @When("the user selects route {string} and clicks {string} route")
    public void routeDeleteAction(String routeName, String action) {
       this.routeName = routeName;
    }


    @Then("the {string} route is saved in the database")
    public void routeSaved(String routeName) {
        // query for route in database
        Favourite savedRoute = (Favourite) SqliteQueryBuilder.create().select("*").from("favourites")
                .where("route_name = \"" + routeName + "\"").buildGetter().get(0);
        Assertions.assertEquals(savedRoute.getName(), routeName);
    }


    @Then("the location {string} has a start location matching {string}")
    public void routeLoaded(String route, String start) {
        // query for route's start address in database and check if equals start
        Favourite savedRoute = (Favourite) SqliteQueryBuilder.create().select("*").from("favourites")
                .where("route_name = \"" + routeName + "\"").buildGetter().get(0);
        Assertions.assertEquals(savedRoute.getName(), routeName);
    }

    @Then("the route {string} is deleted from the database")
    public void routeDeleted(String route) {
        // delete from database
        SqliteQueryBuilder.create().delete("favourites").where("route_name = \"" + routeName + "\"").buildDeleter();
        // query for route name and show it is not in database
        List<?> favouriteList = SqliteQueryBuilder.create()
                .select("*")
                .from("favourites")
                .where("route_name = \"" + routeName + "\"")
                .buildGetter();

        favourite = new Favourite(start, end,
                0, 0, 0,
                0, null, "car", routeName);
        SqliteQueryBuilder.create().insert("favourites").buildSetter(new ArrayList<>(Arrays.asList(favourite)));
    }

}
