package seng202.team0.unittests.models;

import org.junit.jupiter.api.*;
import seng202.team0.models.Region;

import static seng202.team0.models.Region.AUCKLAND;
import static seng202.team0.models.Region.NULL;

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
