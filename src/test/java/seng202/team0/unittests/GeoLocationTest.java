package seng202.team0.unittests;

import org.junit.jupiter.api.Test;
import seng202.team0.models.GeoLocator;
import seng202.team0.models.Location;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class GeoLocationTest {

    /**
     * This test checks the functionality of the {@code getLocation} method in the {@code GeoLocator} class.
     * It validates if the method correctly retrieves the geographical location based on a given address.
     *
     * <p>The test uses a sample address "30 Durey Road" and compares the longitude
     * obtained from {@code getLocation} to the expected longitude value of -43.489444.
     * A delta of 0.1 is used for comparison to account for minor discrepancies.</p>
     *
     * @see GeoLocator#getLocation(String)
     */
    @Test
    void testLocationLongitude(){
        Location location = new Location(-43.489444, 172.532222);
        GeoLocator locater = new GeoLocator();
        Location calcLocation = locater.getLocation("30 Durey Road");

        assertEquals(location.longitude,calcLocation.longitude, 0.1);
    }
    /**
     * This test checks the functionality of the {@code getLocation} method in the {@code GeoLocator} class.
     * It validates if the method correctly retrieves the geographical location based on a given address.
     *
     * <p>The test uses a sample address "30 Durey Road" and compares the latitude
     * obtained from {@code getLocation} to the expected latitude value of -43.489444
     * A delta of 0.1 is used for comparison to account for minor discrepancies.</p>
     *
     * @see GeoLocator#getLocation(String)
     */
    @Test
    void testLocationLatitude(){
        Location location = new Location(-43.489444, 172.532222);
        GeoLocator locater = new GeoLocator();
        Location calcLocation = locater.getLocation("30 Durey Road");

        assertEquals(location.latitude,calcLocation.latitude, 0.1);
    }
}
