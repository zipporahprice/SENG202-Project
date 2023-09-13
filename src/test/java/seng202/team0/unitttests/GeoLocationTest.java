package seng202.team0.unitttests;

import org.junit.jupiter.api.Test;
import seng202.team0.models.GeoLocator;
import seng202.team0.models.Location;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class GeoLocationTest {

    @Test
    void testLocation(){
        Location location = new Location(-43.489444, 172.532222);
        GeoLocator locater = new GeoLocator();
        Location calcLocation = locater.getLocation("30 Durey Road");

        assertEquals(location.longitude,calcLocation.longitude);
    }
}
