package seng202.team0.unittests.models;

import org.junit.jupiter.api.Test;
import seng202.team0.models.GeoLocator;
import seng202.team0.models.Location;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class GeoLocationTest {
    String address = "30 Durey Road";

    /**
     * This test checks the functionality of the {@code getLocation} method in the {@code GeoLocator} class.
     * It validates if the method correctly retrieves the geographical location based on a given address.
     *
     * <p>The test uses a sample address "30 Durey Road" and compares the longitude and latitude
     * obtained from {@code getLocation} to the expected longitude and latitude values.
     * A delta of 0.00001 is used for comparison to account for minor discrepancies.</p>
     *
     * @see GeoLocator#getLocation(String)
     */

    @Test
    void testLocationPair() {
        double delta = 0.00001; // Use an appropriate delta value, which defines the acceptable difference between the actual and expected values
        GeoLocator locator = new GeoLocator();
        Location calcLocation = locator.getLocation(address);
        assertNotNull(calcLocation);  // Ensure the location is not null before proceeding
        assertEquals(-43.488116649999995, calcLocation.getLatitude(), delta);
        assertEquals(172.54213544359874, calcLocation.getLongitude(), delta);
    }

    @Test
    void testGetAddress() {
        Double lat = -43.488116649999995;
        
        Double lng = 172.54213544359874;

        GeoLocator locator = new GeoLocator();

        String addressGotten= locator.getAddress(lat,lng);

        assertEquals(addressGotten, "30 Durey Road Canterbury");
    }
}
