package seng202.team10.cucumber;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import kotlin.Pair;
import seng202.team10.business.RatingAreaManager;



/**
 * Step definitions for Cucumber tests relating to the rating of the areas selected.
 */
public class AreaRatingStepDefinitions {

    RatingAreaManager manager = RatingAreaManager.getInstance();
    String boundingWhere;

    /**
     * Given step to specify a circle for rating,
     * by defining its centre latitude, longitude and radius.
     *
     * @param latitude  String representation of the circle centre latitude.
     * @param longitude String representation of the circle centre longitude.
     * @param radius    String representation of the circle radius.
     */
    @Given("the circle with centre at latitude {string} and longitude {string} and radius {string}")
    public void givenCircle(String latitude, String longitude, String radius) {
        manager.setBoundingCircleCentre(Double.parseDouble(latitude),
                Double.parseDouble(longitude));
        manager.setBoundingCircleRadius(Double.parseDouble(radius));
    }

    /**
     * When step that checks that the boundingWhere is a circle through
     * the SQRT in the query.
     */
    @When("the circle is rated")
    public void circle() {
        boundingWhere = manager.rateAreaHelper();
        assertTrue(boundingWhere.contains("SQRT"));
    }

    /**
     * Given step to specify a rectangle for rating,
     * by defining the minimum and maximum latitude and longitude points.
     *
     * @param minLat  String representation of the minimum latitude.
     * @param minLong String representation of the minimum longitude.
     * @param maxLat  String representation of the maximum latitude.
     * @param maxLong String representation of the maximum longitude.
     */
    @Given("the bounding box with min point at latitude {string} and longitude {string} "
            + "and max point at latitude {string} and longitude {string}")
    public void givenBoundingBox(String minLat, String minLong, String maxLat, String maxLong) {
        manager.setBoundingBoxMin(Double.parseDouble(minLat), Double.parseDouble(minLong));
        manager.setBoundingBoxMax(Double.parseDouble(maxLat), Double.parseDouble(maxLong));
    }

    /**
     * When step that checks if the boundingWhere is a rectangle by
     * checking whether there is a min and max 'X'.
     */
    @When("the rectangle is rated")
    public void rectangle() {
        boundingWhere = manager.rateAreaHelper();
        assertTrue(boundingWhere.contains("minX") && boundingWhere.contains("maxX"));
    }

    /**
     * Asserts that the resulting rating is between 0 and 10 and
     * that the count of crashes used in the rating calculation is non-negative.
     */
    @Then("the rated area is calculated for the area")
    public void ratedArea() {
        Pair<Double, Integer> result = RatingAreaManager.getInstance().queryHelper(boundingWhere);
        assertTrue(result.getFirst() >= 0 && result.getFirst() <= 10);
        assertTrue(result.getSecond() >= 0);
    }
}
