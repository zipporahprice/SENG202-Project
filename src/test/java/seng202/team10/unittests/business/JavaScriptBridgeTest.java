package seng202.team10.unittests.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import seng202.team10.business.JavaScriptBridge;
import seng202.team10.business.SettingsManager;
import seng202.team10.gui.MainController;


class JavaScriptBridgeTest {

    @InjectMocks
    JavaScriptBridge javaScriptBridge;

    @Mock
    MainController mainController;

    @Mock
    JavaScriptBridge.JavaScriptListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        javaScriptBridge.setListener(listener);
        javaScriptBridge.setMainController(mainController);
    }

    @Test
    void testUpdateCrashesByJavascript() {
        List<Map<String, Object>> crashList = Arrays.asList(
                new HashMap<String, Object>() {{
                    put("latitude", 10.0);
                    put("longitude", 20.0);
                    put("severity", 1);
                    put("crash_year", 2022);
                    put("weather", "Clear");
                }}
        );

        System.out.println(crashList.get(0).getClass());

        //String result = JavaScriptBridge.updateCrashesByJavascript(crashList);

        //assertTrue(result.contains("addPoint(10.000000,20.000000,1,2022,'Clear');"));
    }

    @Test
    void testCurrentView() {
        SettingsManager settingsManager = mock(SettingsManager.class);
        when(settingsManager.getCurrentView()).thenReturn("Crash Locations");

        String result = javaScriptBridge.currentView();

        assertEquals("Crash Locations", result);
    }

    @Test
    void testSendCoordinates() {
        String jsonInput = "{"
                + "\"routeId\":1,"
                + "\"coordinates\":[{\"lat\":10,\"lng\":20}],"
                + "\"instructionRoads\":[\"Road1\"],"
                + "\"instructionDistance\":[10]"
                + "}";

        javaScriptBridge.sendCoordinates(jsonInput);
        //not asserted yet
    }

    @Test
    void testMapLoaded() {
        // Action
        javaScriptBridge.mapLoaded();

        // Verification
        verify(listener, times(1)).mapLoaded();
    }

    @Test
    void testEnableRefreshButton() {
        // Action
        javaScriptBridge.enableRefreshButton();

        // Verification
        verify(mainController, times(1)).enableRefresh();
    }

    // Add more tests below...
}
