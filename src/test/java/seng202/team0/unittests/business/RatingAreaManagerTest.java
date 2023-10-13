package seng202.team0.unittests.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team0.business.RatingAreaManager;
import seng202.team0.models.Location;

public class RatingAreaManagerTest {

    @Test
    void testGetInstance() {
        RatingAreaManager manager = RatingAreaManager.getInstance();
        Assertions.assertNotNull(manager);
    }

    @Test
    void testSingleton() {
        RatingAreaManager manager1 = RatingAreaManager.getInstance();
        RatingAreaManager manager2 = RatingAreaManager.getInstance();
        Assertions.assertEquals(manager1, manager2);
    }

    @Test
    void testBoundingBoxMin() {
        RatingAreaManager manager = RatingAreaManager.getInstance();
        manager.setBoundingBoxMin(20.0, 30.0);
        Location boundingBoxMin = manager.getBoundingBoxMin();
        Assertions.assertEquals(20.0, boundingBoxMin.getLatitude());
    }

    @Test
    void testBoundingBoxNull() {
        RatingAreaManager manager = RatingAreaManager.getInstance();
        manager.setBoundingBoxMax(null, null);
        Location boundingBox = manager.getBoundingBoxMax();
        Assertions.assertNull(boundingBox);
    }


}
