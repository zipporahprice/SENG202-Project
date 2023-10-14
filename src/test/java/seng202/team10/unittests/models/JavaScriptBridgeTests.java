package seng202.team10.unittests.models;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import seng202.team10.business.RatingAreaManager;
import seng202.team10.gui.RoutingMenuController;
import seng202.team10.models.JavaScriptBridge;

/**
 * Testing JavaScriptBridge class.
 */

public class JavaScriptBridgeTests {
    private JavaScriptBridge javaScriptBridge;

    @BeforeEach
    void setUp() {

        javaScriptBridge = new JavaScriptBridge();
    }

    @Test
    void setRatingAreaManagerBoundingBox_Test() {
        try (MockedStatic<RatingAreaManager> mockedStatic
                     = Mockito.mockStatic(RatingAreaManager.class)) {
            RatingAreaManager mockRatingAreaManager = mock(RatingAreaManager.class);
            mockedStatic.when(RatingAreaManager::getInstance).thenReturn(mockRatingAreaManager);

            double minLat = 0.0;
            double minLng = 0.0;
            double maxLat = 10.0;
            double maxLng = 10.0;

            javaScriptBridge.setRatingAreaManagerBoundingBox(minLat, minLng, maxLat, maxLng);
            verify(mockRatingAreaManager).setBoundingBoxMin(minLat, minLng);
            verify(mockRatingAreaManager).setBoundingBoxMax(maxLat, maxLng);
            verify(mockRatingAreaManager).setBoundingCircleCentre(null, null);
            verify(mockRatingAreaManager).setBoundingCircleRadius(0);


        }

    }

    @Test
    void setRatingAreaManagerBoundingCircle_Test() {
        try (MockedStatic<RatingAreaManager> mockedStatic
                     = Mockito.mockStatic(RatingAreaManager.class)) {
            RatingAreaManager mockRatingAreaManager = mock(RatingAreaManager.class);
            mockedStatic.when(RatingAreaManager::getInstance).thenReturn(mockRatingAreaManager);

            double latitude = 10.0;
            double longitude = 10.0;
            double radius = 5.0;

            javaScriptBridge.setRatingAreaManagerBoundingCircle(latitude, longitude, radius);
            verify(mockRatingAreaManager).setBoundingCircleCentre(latitude, longitude);
            verify(mockRatingAreaManager).setBoundingCircleRadius(radius);
            verify(mockRatingAreaManager).setBoundingBoxMin(null, null);
            verify(mockRatingAreaManager).setBoundingBoxMax(null, null);


        }

    }

    // @Test
    void testSendCoordinatesCallsRatingUpdate() {
        try (MockedStatic<RoutingMenuController> mocked
                     = Mockito.mockStatic(RoutingMenuController.class)) {
            String jsonInput = "{ \"routeId\": 1, \"coordinates\": [ "
                    + "{\"lat\": 12.34, \"lng\": 56.78}, {\"lat\": 90.12, \"lng\": 34.56} ] }";

            javaScriptBridge.sendCoordinates(jsonInput);


            mocked.verify(RoutingMenuController::ratingUpdate);
        }
    }

    // @Test
    void testSetCrashes() {
        HashMap<String, Object> crashEntry = new HashMap<>();
        crashEntry.put("latitude", 45.0);
        crashEntry.put("longitude", 90.0);
        crashEntry.put("severity", 2);
        crashEntry.put("crash_year", 2020);
        crashEntry.put("weather", "Clear");

        List<HashMap<String, Object>> mockCrashData = new ArrayList<>();
        mockCrashData.add(crashEntry);

        String result = javaScriptBridge.setCrashes();
        System.out.println("This is : " + result);
        assertTrue(result.contains("addPoint(45.000000,90.000000,2,2020,'Clear');"),
                "String does not match expected pattern.");
        assertTrue(result.startsWith("resetLayers();Promise.resolve().then(function () {"),
                "String does not start as expected.");
        assertTrue(result.endsWith("}).then(function () {setHeatmapData();});"),
                "String does not end as expected.");
    }
}
