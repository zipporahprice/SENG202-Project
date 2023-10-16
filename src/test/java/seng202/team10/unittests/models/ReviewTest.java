package seng202.team10.unittests.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import seng202.team10.models.Review;




/**
 * JUnit test class for testing the 'toString' method of the 'Review' class.
 */
public class ReviewTest {

    /**
     * Tests the 'toString' method of the 'Review' class when there are crashes.
     */
    @Test
    public void testToStringWithCrashes() {
        List<HashMap<String, Object>> crashes = new ArrayList<>();
        Review review = new Review(7.5, 8.0, "Snowy", 1999, 2023, 10, "Elm St", crashes);

        String expected = "This route has a 7.50/10 danger rating, "
                + "there have been 10 crashes since 1999 up till 2023. "
                + "The worst crashes occur during Snowy conditions, "
                + "the most dangerous segment is on Elm St with a total severity of 8.00.";

        assertEquals(expected, review.toString());
    }

    /**
     * Tests the 'toString' method of the 'Review' class when there are no crashes.
     */
    @Test
    public void testToStringWithoutCrashes() {
        List<HashMap<String, Object>> crashes = new ArrayList<>();
        Review review = new Review(0.0, 0.0, "Clear", 1999, 2023, -1, "Pine St", crashes);

        String expected = "This route has zero crashes and hence is as safe as can be!";

        assertEquals(expected, review.toString());
    }
}