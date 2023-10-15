package seng202.team10.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import seng202.team10.business.FilterManager;
import seng202.team10.models.CrashSeverity;
import seng202.team10.models.Region;
import seng202.team10.models.Weather;
import seng202.team10.repository.DatabaseManager;
import seng202.team10.repository.SqliteQueryBuilder;

/**
 * Cucumber step definitions for filtering crash data feature.
 */

public class FilteringStepDefinitions {

    private final List<Integer> severitiesList = new ArrayList<>(
            Arrays.stream(CrashSeverity.values()).map(CrashSeverity::getValue).toList()
    );
    private final List<String> weathersList = new ArrayList<>(
            Arrays.stream(Weather.values()).map(weather -> weather.getName()).toList()
    );
    private final List<String> regionsList = new ArrayList<>(
            Arrays.stream(Region.values()).map(region -> region.getName()).toList()
    );
    private final List<Integer> holidaysList = new ArrayList<>(Arrays.asList(0, 1));
    private final List<String> modesList = new ArrayList<>(Arrays.asList(
            "bicycle_involved",
            "bus_involved",
            "car_involved",
            "moped_involved",
            "motorcycle_involved",
            "parked_vehicle_involved",
            "pedestrian_involved",
            "school_bus_involved",
            "train_involved",
            "truck_involved"
    ));

    private String filterColumn;
    private FilterManager filterManager = FilterManager.getInstance();

    /**
     * Given method for initialising filtering.
     *
     * @param filterColumn column the user will filter by.
     */
    @Given("a user wants to filter crashes by {string}")
    public void filterByColumn(String filterColumn) {
        // Make sure the database is reset and initialised
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.resetDb();
        databaseManager.initialiseDatabase("files/crash_data_10k.csv");

        this.filterColumn = filterColumn;

        // Change the singleton instance so that everything needed is set to default
        switch (filterColumn) {
            case "severity" -> severitiesList.forEach(
                    (severity) -> {
                        filterManager.removeFromSeverities(severity);
                        filterManager.addToSeverities(severity);
                    }
            );
            case "weather" -> weathersList.forEach(
                    (weather) -> {
                        filterManager.removeFromWeathers(weather);
                        filterManager.addToWeathers(weather);
                    }
            );
            case "region" -> regionsList.forEach(
                    (region) -> {
                        filterManager.removeFromRegions(region);
                        filterManager.addToRegions(region);
                    }
            );
            case "holiday" -> holidaysList.forEach(
                    (holiday) -> {
                        filterManager.removeFromHolidays(holiday);
                        filterManager.addToHolidays(holiday);
                    }
            );
            case "transport" -> modesList.forEach(
                    (mode) -> {
                        filterManager.removeFromModes(mode);
                        filterManager.addToModes(mode);
                    }
            );
            case "year" -> {
                filterManager.setEarliestYear(2000);
                filterManager.setLatestYear(2023);
            }
            default -> {
                break;
            }
        }
    }

    /**
     * When method for filtering by severity, weather, region, holiday, or transport.
     *
     * @param filterSelections string with filters to be removed
     */
    @When("the user deselects the checkboxes {string}")
    public void filterBySelection(String filterSelections) {
        List<String> filterSelectionsList = List.of(filterSelections.split(","));

        // Deselects the given selections through manager
        switch (filterColumn) {
            case "severity" -> filterSelectionsList.forEach(
                    (severity) -> {
                        filterManager.removeFromSeverities(Integer.parseInt(severity));
                    }
            );
            case "weather" -> filterSelectionsList.forEach(
                    (weather) -> {
                        filterManager.removeFromWeathers(weather);
                    }
            );
            case "region" -> filterSelectionsList.forEach(
                    (region) -> {
                        filterManager.removeFromRegions(region);
                    }
            );
            case "holiday" -> filterSelectionsList.forEach(
                    (holiday) -> {
                        filterManager.removeFromHolidays(Integer.parseInt(holiday));
                    }
            );
            case "transport" -> filterSelectionsList.forEach(
                    (transport) -> {
                        filterManager.removeFromModes(transport + "_involved");
                    }
            );
            default -> {
                break;
            }
        }
    }

    /**
     * When method for filtering by year.
     *
     * @param earliestYear earliestYear to set
     * @param latestYear latestYear to set
     */
    @When("the user sets {int} as the earliest year and {int} as the latest year")
    public void filterByYear(int earliestYear, int latestYear) {
        filterManager.setEarliestYear(earliestYear);
        filterManager.setEarliestYear(latestYear);
    }

    /**
     * Then method for checking crashes were filtered.
     */
    @Then("the user will see crashes without the filters deselected")
    public void crashesShowing() {
        List<?> filterManagerCrashes = SqliteQueryBuilder.create().select("*")
                .from("crashes").where(filterManager.toString()).buildGetter();
        List<?> allCrashes = SqliteQueryBuilder.create().select("*")
                .from("crashes").buildGetter();

        // Assumes that the database when filtering will always have
        // fewer crashes than the whole database. Good for big databases.
        Assertions.assertTrue(filterManagerCrashes.size() < allCrashes.size());
    }

}
