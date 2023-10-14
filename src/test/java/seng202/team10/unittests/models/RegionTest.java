package seng202.team10.unittests.models;

import static seng202.team10.models.Region.AUCKLAND;
import static seng202.team10.models.Region.NULL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team10.models.Region;

/**
 * Testing Region class.
 */

public class RegionTest {

    @Test
    void testStringToRegion() {
        Region region = Region.stringToRegion("Auckland Region");
        Assertions.assertEquals(AUCKLAND, region);
    }

    @Test
    void testNull() {
        Region region = Region.stringToRegion("");
        Assertions.assertEquals(NULL, region);
    }

}
