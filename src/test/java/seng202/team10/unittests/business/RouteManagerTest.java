package seng202.team10.unittests.business;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team10.business.RouteManager;
import seng202.team10.repository.DatabaseManager;
import seng202.team10.repository.SqliteQueryBuilder;

/**
 * Testing RouteManager class.
 */

public class RouteManagerTest {

    @Test
    void testGetInstance() {
        RouteManager routeManager = RouteManager.getInstance();
        Assertions.assertNotNull(routeManager);
    }

    @Test
    void testSingleton() {
        RouteManager routeManager1 = RouteManager.getInstance();
        RouteManager routeManager2 = RouteManager.getInstance();
        Assertions.assertEquals(routeManager1, routeManager2);
    }

    @Test
    void testStartLocationUpdates() {
        RouteManager routeManager = RouteManager.getInstance();
        routeManager.setStartLocation("123 ABC Lane");
        routeManager = RouteManager.getInstance();
        Assertions.assertEquals("123 ABC Lane", routeManager.getStartLocation());
    }

    @Test
    void testStopLocationUpdates() {
        RouteManager routeManager = RouteManager.getInstance();
        routeManager.setStopLocation("321 Queen Street");
        routeManager = RouteManager.getInstance();
        Assertions.assertEquals("321 Queen Street", routeManager.getStopLocation());
    }

    @Test
    void testGetFavourites() {
        DatabaseManager.getInstance().resetDb();
        RouteManager routeManager = RouteManager.getInstance();
        List<?> favourites = routeManager.getFavourites();
        List<?> expectedFavourites = SqliteQueryBuilder.create()
                .select("id").from("favourites").buildGetter();

        Assertions.assertEquals(expectedFavourites.size(), favourites.size());
    }

}
