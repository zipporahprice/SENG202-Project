package seng202.team10.unittests.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team10.business.RouteManager;
import seng202.team10.models.Favourite;
import seng202.team10.models.Location;
import seng202.team10.models.Route;
import seng202.team10.repository.DatabaseManager;
import seng202.team10.repository.SqliteQueryBuilder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing RouteManager class.
 */

public class RouteManagerTest {

    private RouteManager routeManager;

    @BeforeEach
    public void setup() {
        routeManager = RouteManager.getInstance();
    }

    @Test
    public void testHaversineDistance() {
        Location loc1 = new Location(36.12, -86.67);
        Location loc2 = new Location(33.94, -118.40);
        double distance = RouteManager.haversineDistance(loc1, loc2);
        double variation = 1E-4;
        assertEquals(2886444, distance, variation * distance);
    }

    @Test
    public void testGetMaxSeverityWeather() {
        Map<String, Integer> weatherSeverityTotal = new HashMap<>();
        weatherSeverityTotal.put("Sunny", 10);
        weatherSeverityTotal.put("Rainy", 30);
        weatherSeverityTotal.put("Stormy", 20);
        Map<String, Integer> weatherTotals = new HashMap<>();
        weatherTotals.put("Sunny", 2);
        weatherTotals.put("Rainy", 3);
        weatherTotals.put("Stormy", 4);
        String maxWeather = RouteManager.getMaxSeverityWeather(weatherSeverityTotal, weatherTotals);
        assertEquals("Rainy", maxWeather);
    }

    @Test
    public void testCalculateDanger() {
        int setSize = 5;
        double totalValue = 15;
        Pair<Integer, Double> result = RouteManager.calculateDanger(setSize, totalValue);
        assertNotNull(result);
        assertEquals(5, result.getKey());
        assertTrue(result.getValue() >= 0 && result.getValue() <= 10);
    }

    @Test
    void testFavouriteNames() {
        // Setup to make sure database is empty
        DatabaseManager.getInstance().resetDb();

        // Adding a favourite
        String favouriteName = "Home";
        Favourite favourite = new Favourite("start", "end", 20.0,
                30.0, 40.0, 50.0, "", "", favouriteName);
        SqliteQueryBuilder.create().insert("favourites").buildSetter(List.of(favourite));

        // Check names has the new favourite's name
        List<?> favouriteNames = RouteManager.getFavouriteNames();
        Assertions.assertEquals(((HashMap<?, ?>)favouriteNames.get(0)).get("route_name"), favouriteName);

        // Tear down to make sure database is fresh without this favourite
        DatabaseManager.getInstance().resetDb();
    }

    @Test
    public void testBoundingBoxSegmentSearch() {
        Location startLocation = new Location(-36.8485, 174.7633);
        Location endLocation = new Location(-37.7870, 175.2793);
        List<?> result = RouteManager.boundingBoxSegmentSearch(startLocation, endLocation);
        assertNotNull(result);
    }

    @Test
    void testGetInstance() {
        assertNotNull(routeManager);
    }

    @Test
    void testSingleton() {
        RouteManager routeManager1 = RouteManager.getInstance();
        RouteManager routeManager2 = RouteManager.getInstance();
        assertEquals(routeManager1, routeManager2);
    }

    @Test
    void testStartLocationUpdates() {
        routeManager.setStartLocation("123 ABC Lane");
        routeManager = RouteManager.getInstance();
        assertEquals("123 ABC Lane", routeManager.getStartLocation());
    }

    @Test
    void testStopLocationUpdates() {
        routeManager.setStopLocation("321 Queen Street");
        routeManager = RouteManager.getInstance();
        assertEquals("321 Queen Street", routeManager.getStopLocation());
    }

}
