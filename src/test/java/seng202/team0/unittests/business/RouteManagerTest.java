package seng202.team0.unittests.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team0.business.RouteManager;

public class RouteManagerTest {

    @Test
    void testGetInstance() {
        RouteManager routeManager = RouteManager.getInstance();
        Assertions.assertTrue(routeManager instanceof RouteManager);
    }

}
