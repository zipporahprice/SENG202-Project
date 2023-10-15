package seng202.team10.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team10.business.RouteManager;
import seng202.team10.models.Favourite;
import seng202.team10.repository.DatabaseManager;
import seng202.team10.repository.SqliteQueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains step definitions for testing favourite routes using Cucumber.
 * These definitions map to statements in Cucumber feature files and
 * provide the logic to execute those steps.
 *
 * @author Zipporah Price
 */
public class FavouriteStepDefinitions {
    private String routeName;
    private Favourite favourite;
    private String start;
    private String end;
    private RouteManager routeManager = RouteManager.getInstance();


    /**
     * Sets up initial conditions of initialising the database,
     * the databaseManager and setting a start and end location address.
     *
     * @param startLocation String representation of a start address.
     * @param endLocation String representation of an end address.
     */
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

    /**
     * Sets up initial conditions of initialising the database,
     * the databaseManager and setting a start and end location address.
     * Creates a Favourite from the given information and inserts it into the database.
     *
     * @param routeName The name given to a favourite route by a user.
     * @param start The start location address for the favourite.
     */
    @Given("there is a route saved called {string} with starting location {string}")
    public void favouriteExists(String routeName, String start) {
        // Make sure the database is reset and initialised
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.resetDb();
        databaseManager.initialiseDatabase("files/crash_data_10k.csv");
        this.routeName = routeName;
        this.start = start;

        // insert a route named into database
        favourite = new Favourite(start, end,
                0, 0, 0,
                0, null, "car", routeName);
        SqliteQueryBuilder.create().insert("favourites").buildSetter(new ArrayList<>(Arrays.asList(favourite)));
    }


    /**
     * Assigns the routeName to the stored variable.
     *
     * @param routeName The name given to a favourite route by a user.
     */
    @When("the user clicks save route and enters a unique {string}")
    public void namedRoute(String routeName) {
        this.routeName = routeName;
    }

    /**
     * Assigns the routeName to the stored variable.
     *
     * @param routeName The name given to a favourite route by a user.
     * @param action Action chosen by user - either 'delete' or 'load'.
     */
    @When("the user selects route {string} and clicks {string} route")
    public void routeDeleteAction(String routeName, String action) {
       this.routeName = routeName;
    }


    /**
     * Takes the given route name and creates a favourite using this.
     * Stores this favourite in the database,
     * then checks that the database includes the favourite.
     *
     * @param routeName The name given to a favourite route by a user.
     */
    @Then("the {string} route is saved in the database")
    public void routeSaved(String routeName) {
        // insert route into database
        favourite = new Favourite(start, end,
                0, 0, 0,
                0, null, "car", routeName);
        SqliteQueryBuilder.create().insert("favourites").buildSetter(new ArrayList<>(Arrays.asList(favourite)));

        // query for route in database
        Favourite savedRoute = (Favourite) SqliteQueryBuilder.create().select("*").from("favourites")
                .where("route_name = \"" + routeName + "\"").buildGetter().get(0);
        Assertions.assertEquals(savedRoute.getName(), routeName);
    }


    /**
     * Queries the database to get the favourite route by the given name.
     * Asserts that the queried route is the same route asked for.
     *
     * @param routeName The name given to a favourite route by a user.
     * @param start String representation of the favourite's start address.
     */
    @Then("the location {string} has a start location matching {string}")
    public void routeLoaded(String routeName, String start) {
        // query for route's start address in database and check if equals start
        Favourite savedRoute = (Favourite) SqliteQueryBuilder.create().select("*").from("favourites")
                .where("route_name = \"" + routeName + "\"").buildGetter().get(0);
        Assertions.assertEquals(savedRoute.getName(), routeName);
    }

    /**
     * Deletes the route by the given name from the database.
     * Queries the database to check it contains no route under given name.
     *
     * @param routeName The name given to a favourite route by a user.
     */
    @Then("the route {string} is deleted from the database")
    public void routeDeleted(String routeName) {
        // delete from database
        SqliteQueryBuilder.create().delete("favourites").where("route_name = \"" + routeName + "\"").buildDeleter();
        // query for route name and show it is not in database
        List<?> favouriteList = SqliteQueryBuilder.create()
                .select("*")
                .from("favourites")
                .where("route_name = \"" + routeName + "\"")
                .buildGetter();

        Assertions.assertTrue(favouriteList.isEmpty());
    }

}
