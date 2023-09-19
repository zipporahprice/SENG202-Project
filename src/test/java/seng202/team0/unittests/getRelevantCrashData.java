package seng202.team0.unittests;

import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.models.Crash;
import seng202.team0.models.CrashSeverity;
import seng202.team0.models.Weather;

import static org.junit.jupiter.api.Assertions.*;

public class getRelevantCrashData {

    private Crash crash;

    @BeforeEach
    public void setUp() {
        crash = new Crash(1, 50, 2020, "Location1", "Location2", "Severe", "Region1", "Sunny", 10.5f, 20.5f,
                true, false, true, false, false, true,
                false, true, false, false, true);
    }

    @Test
    public void testGetObjectId() {
        assertEquals(1, crash.getObjectId());
    }

    @Test
    public void testGetSpeedLimit() {
        assertEquals(50, crash.getSpeedLimit());
    }

    @Test
    public void testGetCrashYear() {
        assertEquals(2020, crash.getCrashYear());
    }

    @Test
    public void testGetCrashLocation1() {
        assertEquals("Location1", crash.getCrashLocation1());
    }

    @Test
    public void testGetCrashLocation2() {
        assertEquals("Location2", crash.getCrashLocation2());
    }

    @Test
    public void testGetSeverity() {
        assertEquals(CrashSeverity.stringToCrashSeverity("Severe"), crash.getSeverity());
    }

    @Test
    public void testGetRegion() {
        assertEquals("Region1", crash.getRegion());
    }

    @Test
    public void testGetWeather() {
        assertEquals(Weather.stringToWeather("Sunny"), crash.getWeather());
    }

    @Test
    public void testGetLongitudeAndLatitude() {
        Pair<Float, Float> expected = new Pair<>(10.5f, 20.5f);
        assertEquals(expected, crash.getLongitudeAndLatitude());
    }

    @Test void testGetLongitude() { assertEquals(crash.getLongitude(),10.5F);}
    @Test void testGetLatitude() { assertEquals(crash.getLatitude(),20.5F);}
    @Test
    public void testIsBicycleInvolved() {
        assertTrue(crash.isBicycleInvolved());
    }

    @Test
    public void testIsTruckInvolved() {assertTrue(crash.isTruckInvolved());}

    @Test
    public void testIsSchoolBus() {assertFalse(crash.isSchoolBusInvolved());}

    @Test
    public void testIsBusInvolved() {assertFalse(crash.isBusInvolved());}

    @Test
    public void testIsTrainInvolved() {assertFalse(crash.isTrainInvolved());}

    @Test
    public void testIsCarInvolved() {assertTrue(crash.isTruckInvolved());}

    @Test
    public void isMotorcycleInvolved() {assertTrue(crash.isMotorcycleInvolved());}

    @Test
    public void testIsHoliday() {assertFalse(crash.isHoliday());}

    @Test
    public void testIsPedestrianInvolved() {assertTrue(crash.isPedestrianInvolved());}

    @Test
    public void testIsParkedVehicleInvolved() {assertFalse(crash.isParkedVehicleInvolved());}


    @Test
    public void testIsMopedInvolved() {assertFalse(crash.isMopedInvolved());}




    // You can continue to add tests for other boolean fields and methods as well.
}
