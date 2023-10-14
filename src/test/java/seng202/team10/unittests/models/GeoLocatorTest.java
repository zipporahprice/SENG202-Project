package seng202.team10.unittests.models;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import seng202.team10.models.GeoLocator;
import seng202.team10.models.Location;

/**
 * Testing GeoLocator class.
 */

public class GeoLocatorTest {

    @Mock
    HttpClient client;

    @Mock
    HttpResponse<String> response;

    @InjectMocks
    GeoLocator geoLocatorInstance;

    String address = "30 Durey Road";


    /**
     * This test checks the functionality of the {@code getLocation}
     * method in the {@code GeoLocator} class.
     * It validates if the method correctly retrieves the geographical
     * location based on a given address.
     *
     * <p>The test uses a sample address "30 Durey Road" and compares the longitude and latitude
     * obtained from {@code getLocation} to the expected longitude and latitude values.
     * A delta of 0.00001 is used for comparison to account for minor discrepancies.</p>
     *
     * @see GeoLocator#getLocation(String, String)
     */

    // TODO fix this test
    // @Test
    void testLocationPair() {
        // Use an appropriate delta value, which defines the acceptable difference
        // between the actual and expected values
        double delta = 0.00001;
        GeoLocator locator = new GeoLocator();
        Pair<Location, String> calcLocation = locator.getLocation(address);
        Location location =  calcLocation.getKey();
        assertNotNull(calcLocation);  // Ensure the location is not null before proceeding
        assertEquals(-43.488116649999995, location.getLatitude(), delta);
        assertEquals(172.54213544359874, location.getLongitude(), delta);
    }

    @Test
    void testGetAddress() {
        Double lat = -43.488116649999995;
        
        Double lng = 172.54213544359874;

        GeoLocator locator = new GeoLocator();

        String addressGotten = locator.getAddress(lat, lng, "Start");

        assertEquals(addressGotten, "30 Durey Road Canterbury");
    }

}
