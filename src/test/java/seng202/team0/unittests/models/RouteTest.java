package seng202.team0.unittests.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team0.models.Location;
import seng202.team0.models.Route;

import java.util.Arrays;
import java.util.List;

public class RouteTest {

    @Test
    void testConstructor() {
        List<Location> points = Arrays.asList(new Location(40.7128, -74.0060), new Location(34.0522, -118.2437));
        Route route = new Route(points);
        Assertions.assertEquals(points, route.route, "The route's points should match the provided points.");
    }

    @Test
    void testToJSONArray() {
        List<Location> points = Arrays.asList(new Location(40.7128, -74.0060));
        Route route = new Route(points);
        String expectedJson = "[{\"lat\": 40.712800, \"lng\": -74.006000}]";
        Assertions.assertEquals(expectedJson, route.toJsonArray(), "The JSON output should match the expected format.");
    }

    @Test
    void testRoutesToJSONArray() {
        List<Location> points1 = Arrays.asList(new Location(40.7128, -74.0060));
        List<Location> points2 = Arrays.asList(new Location(34.0522, -118.2437));
        Route route1 = new Route(points1);
        Route route2 = new Route(points2);

        List<Route> routes = Arrays.asList(route1, route2);
        String expectedJson = "[[{\"lat\": 40.712800, \"lng\": -74.006000}], [{\"lat\": 34.052200, \"lng\": -118.243700}]]";
        Assertions.assertEquals(expectedJson, Route.routesToJSONArray(routes), "The JSON output should match the expected format for multiple routes.");
    }
}

