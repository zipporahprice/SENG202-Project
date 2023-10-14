package seng202.team10.unittests.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import seng202.team10.models.Region;

/**
 * Testing Region enum.
 */

public class RegionTest {

    /**
     * Parameterized tests for stringToRegion method.
     * @param input string to test.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "Auckland Region", "Bay of Plenty Region", "Canterbury Region",
            "Gisborne Region", "Hawke's Bay Region", "Manawatū-Whanganui Region",
            "Marlborough Region", "Nelson Region", "Northland Region",
            "Otago Region", "Southland Region", "Taranaki Region",
            "Tasman Region", "Waikato Region", "Wellington Region",
            "West Coast Region", "Unknown Region"
    })
    void testStringToRegion(String input) {
        Region actualRegion = Region.stringToRegion(input);
        Region expectedRegion = switch (input) {
            case "Auckland Region" -> Region.AUCKLAND;
            case "Bay of Plenty Region" -> Region.BAYOFPLENTY;
            case "Canterbury Region" -> Region.CANTERBURY;
            case "Gisborne Region" -> Region.GISBORNE;
            case "Hawke's Bay Region" -> Region.HAWKESBAY;
            case "Manawatū-Whanganui Region" -> Region.MANAWATUWHANGANUI;
            case "Marlborough Region" -> Region.MARLBOROUGH;
            case "Nelson Region" -> Region.NELSON;
            case "Northland Region" -> Region.NORTHLAND;
            case "Otago Region" -> Region.OTAGO;
            case "Southland Region" -> Region.SOUTHLAND;
            case "Taranaki Region" -> Region.TARANAKI;
            case "Tasman Region" -> Region.TASMAN;
            case "Waikato Region" -> Region.WAIKATO;
            case "Wellington Region" -> Region.WELLINGTON;
            case "West Coast Region" -> Region.WESTCOAST;
            default -> Region.NULL;
        };

        Assertions.assertEquals(expectedRegion, actualRegion);
    }

    /**
     * Testing getName method.
     */
    @Test
    void testGetName() {
        Region region = Region.AUCKLAND;
        String regionName = region.getName();
        Assertions.assertEquals("Auckland", regionName);
    }

}
