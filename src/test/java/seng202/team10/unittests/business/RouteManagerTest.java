package seng202.team10.unittests.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team10.business.RouteManager;
import seng202.team10.models.Favourite;
import seng202.team10.models.Location;
import seng202.team10.models.Review;
import seng202.team10.repository.DatabaseManager;
import seng202.team10.repository.SqliteQueryBuilder;




/**
 * Testing RouteManager class.
 */

public class RouteManagerTest {
    private static final Logger log = LogManager.getLogger(RouteManagerTest.class);

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
        Assertions.assertEquals(((HashMap<?, ?> ) favouriteNames.get(0))
                .get("route_name"), favouriteName);

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
    void testGetOverlappingPoints() {
        // Coordinates, roads, and distance arrays from a route ran through GUI
        List<Location> coordinatesList = Arrays.asList(
                new Location(-43.52237, 172.53116), new Location(-43.52239, 172.53119),
                new Location(-43.52243, 172.53126), new Location(-43.52246, 172.53135),
                new Location(-43.52249, 172.53143), new Location(-43.5226, 172.53179),
                new Location(-43.52263, 172.53189), new Location(-43.52264, 172.53194),
                new Location(-43.52266, 172.53197), new Location(-43.52266, 172.53197),
                new Location(-43.52264, 172.53199), new Location(-43.52263, 172.53201),
                new Location(-43.52262, 172.53204), new Location(-43.52262, 172.53207),
                new Location(-43.52262, 172.53209), new Location(-43.52264, 172.53212),
                new Location(-43.52265, 172.53213), new Location(-43.52273, 172.53255),
                new Location(-43.52275, 172.53264), new Location(-43.52276, 172.53272),
                new Location(-43.52276, 172.53272), new Location(-43.52253, 172.53283),
                new Location(-43.52234, 172.53292), new Location(-43.52223, 172.53298),
                new Location(-43.52219, 172.53301), new Location(-43.52201, 172.53311),
                new Location(-43.52186, 172.53319), new Location(-43.52133, 172.53349),
                new Location(-43.52128, 172.53353), new Location(-43.52117, 172.53361),
                new Location(-43.52089, 172.53376), new Location(-43.52083, 172.53378),
                new Location(-43.5205, 172.53397), new Location(-43.52047, 172.53399),
                new Location(-43.52039, 172.53403), new Location(-43.5203, 172.53408),
                new Location(-43.5203, 172.53408), new Location(-43.52033, 172.53419),
                new Location(-43.52035, 172.53426), new Location(-43.52036, 172.53431),
                new Location(-43.52042, 172.53452), new Location(-43.52055, 172.53491),
                new Location(-43.52088, 172.53594), new Location(-43.52125, 172.53714),
                new Location(-43.52144, 172.5378), new Location(-43.52144, 172.5378),
                new Location(-43.5209, 172.53812), new Location(-43.5209, 172.53812),
                new Location(-43.5209, 172.53812)
        );

        List<String> roadsList = Arrays.asList(
                "Cathedral Square", "Cathedral Square", "Cathedral Square",
                "Saint Asaph Street; St Asaph Street",
                "Durham Street South", "Brougham Street", "Elizabeth Avenue", "Elizabeth Avenue",
                "Elizabeth Avenue", "Hall Street", "Main Street", "Main Street"
        );

        List<Double> distancesList = Arrays.asList(
                83.765, 131.489, 654.237, 242.827, 1244.02, 56770.266,
                158.019, 107.24, 37194.621, 110.901, 167.973, 0.0
        );

        Review review = RouteManager.getOverlappingPoints(coordinatesList,
                roadsList, distancesList);
        Assertions.assertTrue(review.getDangerRating() >= 0 && review.getDangerRating() <= 10);
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
