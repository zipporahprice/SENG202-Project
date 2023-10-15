package seng202.team10.cucumber;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.List;
import seng202.team10.business.RouteManager;
import seng202.team10.models.Location;
import seng202.team10.models.Review;

/**
 * Contains step definitions for testing route reviews using Cucumber.
 * These definitions map to statements in Cucumber feature files and
 * provide the logic to execute those steps.
 */
public class RouteReviewStepDefinitions {

    List<Location> coordinates;
    List<String> roads;
    List<Double> distances;
    Review review;

    /**
     * Sets up initial conditions with example coordinates, roads, and distances.
     */
    @Given("the user has a set of coordinates, roads, and distances")
    public void startingCoords() {
        // Initialize the coordinates, roads, and distances lists here.
        coordinates = new ArrayList<>();
        roads = new ArrayList<>();
        distances = new ArrayList<>();

        // Example data:
        coordinates.add(new Location(164.1, 73.14));
        coordinates.add(new Location(165.1, 74.14));
        roads.add("Example Road");
        distances.add(5.0);
    }

    /**
     * Simulates user action of generating a route review.
     */
    @When("the user generates a route")
    public void reviewRoute() {
        review = RouteManager.getOverlappingPoints(coordinates, roads, distances);
    }

    /**
     * Asserts all of the variables in the generated review.
     */
    @Then("the user should receive a review containing relevant metrics")
    public void receiveMetrics() {
        assertNotNull(review);
        assertTrue(review.getDangerRating() >= 0 && review.getDangerRating() <= 10);
        if (review.getTotalNumPoints() == -1) {
            assertEquals(review.toString(), "This route has zero crashes and hence is as safe"
                    + " as can be!");
        } else {
            assertTrue(review.maxSegmentSeverity >= 0);
            assertTrue(review.getEndYear() >= 2000 && review.getEndYear() <= 2023);
            assertTrue(review.getEndYear() >= 2000 && review.getEndYear() <= 2023);
            assertEquals(review.getFinalRoad(), "Example Road");
        }
    }
}
