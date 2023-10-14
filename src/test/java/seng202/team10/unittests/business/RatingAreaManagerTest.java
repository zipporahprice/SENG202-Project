package seng202.team10.unittests.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team10.business.RatingAreaManager;
import seng202.team10.models.Location;

public class RatingAreaManagerTest {
    private RatingAreaManager manager;

    /**
     * Set up method for each test that gets the RatingAreaManager singleton instance.
     */
    @BeforeEach
    void setUp() {
        manager = RatingAreaManager.getInstance();
    }

    /**
     * Testing getInstance method.
     */
    @Test
    void testGetInstance() {
        Assertions.assertNotNull(manager);
    }

    /**
     * Testing singleton nature of class.
     */
    @Test
    void testSingleton() {
        RatingAreaManager anotherManager = RatingAreaManager.getInstance();
        Assertions.assertEquals(manager, anotherManager);
    }

    /**
     * Testing boundingBoxMin method with numerical values.
     */
    @Test
    void testBoundingBoxMin() {
        double minLatitude = 20.0;
        double minLongitude = 30.0;
        manager.setBoundingBoxMin(minLatitude, minLongitude);
        Location boundingBoxMin = manager.getBoundingBoxMin();
        Assertions.assertTrue(minLatitude == boundingBoxMin.getLatitude()
                && minLongitude == boundingBoxMin.getLongitude());
    }

    /**
     * Testing boundingBoxMax method with numerical values.
     */
    @Test
    void testBoundingBoxMax() {
        double maxLatitude = 20.0;
        double maxLongitude = 30.0;
        manager.setBoundingBoxMax(maxLatitude, maxLongitude);
        Location boundingBoxMax = manager.getBoundingBoxMax();
        Assertions.assertTrue(maxLatitude == boundingBoxMax.getLatitude()
                && maxLongitude == boundingBoxMax.getLongitude());
    }

    /**
     * Testing boundingCircleCentre method with numerical values.
     */
    @Test
    void testBoundingCircleCentre() {
        double centreLatitude = 20.0;
        double centreLongitude = 30.0;
        manager.setBoundingCircleCentre(centreLatitude, centreLongitude);
        Location boundingCircleCentre = manager.getBoundingCircleCentre();
        Assertions.assertTrue(centreLatitude == boundingCircleCentre.getLatitude()
                && centreLongitude == boundingCircleCentre.getLongitude());
    }

    @Test
    void testBoundingCircleRadius() {
        double circleRadius = 20.0;
        manager.setBoundingCircleRadius(circleRadius);
        double boundingCircleRadius = manager.getBoundingCircleRadius();
        Assertions.assertEquals(circleRadius, boundingCircleRadius);
    }

    /**
     * Testing boundingBoxMin method with null parameters.
     */
    @Test
    void testBoundingBoxMinNull() {
        manager.setBoundingBoxMin(null, null);
        Location boundingBox = manager.getBoundingBoxMax();
        Assertions.assertNull(boundingBox);
    }

    /**
     * Testing boundingBoxMax method with null parameters.
     */
    @Test
    void testBoundingBoxMaxNull() {
        manager.setBoundingBoxMax(null, null);
        Location boundingBox = manager.getBoundingBoxMax();
        Assertions.assertNull(boundingBox);
    }

    /**
     * Tesying boundingCircleCentre method with null parameters.
     */
    @Test
    void testBoundingCircleCentreNull() {
        manager.setBoundingCircleCentre(null, null);
        Location boundingBox = manager.getBoundingCircleCentre();
        Assertions.assertNull(boundingBox);
    }


}
