package unitttests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team0.models.GeoLocater;
import seng202.team0.models.Location;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class GeoLocationTest {

    @Test
    public void testLocation(){
        Location location = new Location(-43.489444, 172.532222);
        GeoLocater locater = new GeoLocater();
        Location calcLocation = locater.getLocation("30 Durey Road");

        assertEquals(location.longitude,calcLocation.longitude);
    }
}
