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
 *
 * This class defines steps for BDD tests, interacting with the FilterManager
 * and SqliteQueryBuilder to validate the crash data filtering functionality
 * of the application.
 *
 * @author team 10
 */
public class FilterCrashDataStepDefinitions {

    String where;
    @Given("the user wants to see crashes with a {string}")
    public void crashChosen(String filter) {
        where = filter;
    }

    @When("the user clicks the checkbox and changes the {string} filter")
    public void crashClicked(String type) {
        // TODO add more modes to be fine with any scenario When
        // TODO not actually using FilterManager
        if (type == "transport_mode") {
            FilterManager.getInstance();
            FilterManager.getInstance().updateFiltersWithQueryString("");
            FilterManager.getInstance().addToModes(where);
        }
    }

    @Then("the user will see less crashes than the size of the {string} table")
    public void checkFilteringWorked(String table) {
         List filtered = SqliteQueryBuilder.create().select("*")
                .from(table).where(where + " = 1").buildGetter();
         List allPoints = SqliteQueryBuilder.create().select("*")
                 .from(table).buildGetter();

         // TODO maybe look at if filtered actually has points??
         Assertions.assertTrue(filtered.size() < allPoints.size());
    }
}
