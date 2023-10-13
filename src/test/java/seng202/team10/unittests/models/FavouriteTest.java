package seng202.team10.unittests.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team10.models.Favourite;

public class FavouriteTest {
    private Favourite favourite;

    // Test data
    private final String startAddress = "30 Durey Road";
    private final String endAddress = "30 Durey Road";
    private final double startLat = -43.488116649999995;
    private final double startLong = 172.54213544359874;
    private final double endLat = -43.488116649999995;
    private final double endLong = 172.54213544359874;
    private final String filters = "Some Filters";
    private final String transportMode = "car";

    @BeforeEach
    void setUp() {
        favourite = new Favourite(startAddress, endAddress, startLat, startLong, endLat, endLong, filters, transportMode);
    }


    @Test
    void testGetStartLat() {
        double result = favourite.getStartLat();
        Assertions.assertEquals(startLat, result);
    }

    @Test
    void testGetStartLong() {
        double result = favourite.getStartLong();
        Assertions.assertEquals(startLong, result);
    }

    @Test
    void testGetEndLat() {
        double result = favourite.getEndLat();
        Assertions.assertEquals(endLat, result);
    }

    @Test
    void testGetEndLong() {
        double result = favourite.getEndLong();
        Assertions.assertEquals(endLong, result);
    }

}
