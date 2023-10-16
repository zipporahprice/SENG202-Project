package seng202.team10.unittests.business;

import java.io.File;
import java.net.URL;
import kotlin.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team10.business.RatingAreaManager;
import seng202.team10.exceptions.DataImportException;
import seng202.team10.models.Location;
import seng202.team10.repository.DatabaseManager;

/**
 * Testing RatingAreaManager test.
 */

public class RatingAreaManagerTest {
    private static final Logger log = LogManager.getLogger(RatingAreaManagerTest.class);
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
     * Testing boundingCircleCentre method with null parameters.
     */
    @Test
    void testBoundingCircleCentreNull() {
        manager.setBoundingCircleCentre(null, null);
        Location boundingBox = manager.getBoundingCircleCentre();
        Assertions.assertNull(boundingBox);
    }

    /**
     * Testing clearingBoundingBoxes method.
     */
    @Test
    void testClearingBoundingBoxes() {
        manager.clearBoundingBoxes();
        Assertions.assertTrue(manager.getBoundingBoxMax() == null
                && manager.getBoundingBoxMin() == null);
    }

    /**
     * Testing rateAreaHelper method with rectangle.
     */
    @Test
    void testRateAreaHelperWithRectangle() {
        // Set rectangle to be visible
        manager.setBoundingBoxMin(10.0, 20.0);
        manager.setBoundingBoxMax(20.0, 30.0);

        // Set circle to be invisible
        manager.setBoundingCircleCentre(null, null);

        String rateAreaResult = manager.rateAreaHelper();

        // As a rectangle, the result should not have
        // SQRT since that is particular to circle rating
        Assertions.assertTrue(!rateAreaResult.contains("SQRT")
                && !rateAreaResult.isEmpty());
    }

    /**
     * Testing rateAreaHelper method with circle.
     */
    @Test
    void testRateAreaHelperWithCircle() {
        // Set circle to be visible
        manager.setBoundingCircleCentre(20.0, 30.0);
        manager.setBoundingCircleRadius(1.0);

        // Set rectangle to be invisible
        manager.setBoundingBoxMin(null, null);
        manager.setBoundingBoxMax(null, null);

        String rateAreaResult = manager.rateAreaHelper();

        // As a circle, the result should have
        // SQRT since that is particular to circle rating
        Assertions.assertTrue(rateAreaResult.contains("SQRT"));
    }

    /**
     * Testing queryHelper method.
     */
    @Test
    void testQueryHelper() {
        // Random coordinates in Christchurch
        manager.setBoundingBoxMin(-43.539241, 172.502283);
        manager.setBoundingBoxMax(-43.499899, 172.620069);

        databaseReset();

        Pair<Double, Integer> scoreTotalPair = manager.queryHelper(manager.rateAreaHelper());
        Assertions.assertTrue(scoreTotalPair.getFirst() >= 0
                && scoreTotalPair.getFirst() <= 10 && scoreTotalPair.getSecond() >= 0);

    }

    /**
     * Helper function that resets and initialises the database with 10k file.
     */
    private void databaseReset() {
        // Setting up file to import
        URL newUrl = Thread.currentThread().getContextClassLoader()
                .getResource("files/crash_data_10k.csv");
        File testFile = new File(newUrl.getPath());

        // Reset the database and initialise to make sure points are in there for queryHelper
        try {
            DatabaseManager.getInstance().resetDb();
            DatabaseManager.getInstance().importFile(testFile);
        } catch (DataImportException e) {
            log.error(e);
        }
    }
}
