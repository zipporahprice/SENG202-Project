package seng202.team0.unittests.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team0.business.RouteManager;

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

}
