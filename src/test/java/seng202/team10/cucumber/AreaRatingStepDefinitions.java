package seng202.team10.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import kotlin.Pair;
import seng202.team10.business.RatingAreaManager;

import static org.junit.Assert.*;

public class AreaRatingStepDefinitions {

    RatingAreaManager manager = RatingAreaManager.getInstance();
    String boundingWhere;

    @Given("the circle with centre at latitude {string} and longitude {string} and radius {string}")
    public void givenCircle(String latitude, String longitude, String radius) {
        manager.setBoundingCircleCentre(Double.parseDouble(latitude), Double.parseDouble(longitude));
        manager.setBoundingCircleRadius(Double.parseDouble(radius));
    }

    @When("the circle is rated")
    public void circle() {
        boundingWhere = manager.rateAreaHelper();
        assertTrue(boundingWhere.contains("SQRT"));
    }

    @Given("the bounding box with min point at latitude {string} and longitude {string} and max point at latitude {string} and longitude {string}")
    public void givenBoundingBox(String minLat, String minLong, String maxLat, String maxLong) {
        manager.setBoundingBoxMin(Double.parseDouble(minLat), Double.parseDouble(minLong));
        manager.setBoundingBoxMax(Double.parseDouble(maxLat), Double.parseDouble(maxLong));
    }

    @When("the rectangle is rated")
    public void rectangle() {
        boundingWhere = manager.rateAreaHelper();
        assertTrue(boundingWhere.contains("minX") && boundingWhere.contains("maxX"));
    }


    @Then("the rated area is calculated for the area")
    public void ratedArea() {
        Pair<Double, Integer> result = RatingAreaManager.queryHelper(boundingWhere);
        assertTrue(result.getFirst() >= 0 && result.getFirst() <= 10);
        assertTrue(result.getSecond() >= 0);
    }
}
