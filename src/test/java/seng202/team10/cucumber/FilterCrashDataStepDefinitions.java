package seng202.team10.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import seng202.team10.business.FilterManager;
import seng202.team10.repository.SqliteQueryBuilder;

/**
 * Provides Cucumber step definitions for filtering crash data scenarios.
 * This class defines steps for BDD tests, interacting with the FilterManager
 * and SqliteQueryBuilder to validate the crash data filtering functionality
 * of the application.
 *
 * @author team 10
 */
public class FilterCrashDataStepDefinitions {

    String where;

    /**
     * Setups the filtering criteria based on user's input.
     *
     * @param filter The filtering condition as a string.
     */
    @Given("the user wants to see crashes with a {string}")
    public void crashChosen(String filter) {
        where = filter;
    }

    /**
     * Simulates user action of selecting and applying a filter.
     *
     * @param type The type of filter being applied.
     */
    @When("the user clicks the checkbox and changes the {string} filter")
    public void crashClicked(String type) {
        if (type.equals("transport_mode")) {
            FilterManager.getInstance();
            FilterManager.getInstance().updateFiltersWithQueryString("");
            FilterManager.getInstance().addToModes(where);
        }
    }

    /**
     * Validates that applying the filter reduces the size of the displayed data.
     *
     * @param table The database table name as a string.
     */
    @Then("the user will see less crashes than the size of the {string} table")
    public void checkFilteringWorked(String table) {
        List filtered = SqliteQueryBuilder.create().select("*")
                .from(table).where(where + " = 1").buildGetter();
        List allPoints = SqliteQueryBuilder.create().select("*")
                .from(table).buildGetter();

        Assertions.assertTrue(filtered.size() < allPoints.size());
    }
}
