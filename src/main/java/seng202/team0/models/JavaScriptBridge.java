package seng202.team0.models;

import com.google.gson.Gson;

import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.controlsfx.control.Rating;
import seng202.team0.business.CrashManager;
import seng202.team0.business.FilterManager;
import seng202.team0.business.RatingAreaManager;
import seng202.team0.gui.MainController;
import seng202.team0.gui.SettingsMenuController;



/**
 * Provides a bridge between JavaScript and Java for handling crash data.
 * This class retrieves crash data, converts it to a JSON format, and exposes it
 * to JavaScript for integration with web applications.
 *
 * @author Angelica Silva
 * @author Christopher Wareing
 * @author Neil Alombro
 * @author Todd Vermeir
 * @author William Thompson
 * @author Zipporah Price
 *
 */
public class JavaScriptBridge {
    private CrashManager crashData = new CrashManager();
    private String currentView;

    private JavaScriptListener listener;

    private MainController mainController;


    public void setListener(JavaScriptListener listener) {
        this.listener = listener;
    }

    /**
     * Retrieves a list of crash data and converts it to a JSON format.
     *
     * @return A JSON representation of crash data containing latitude and longitude information.
     * @throws SQLException If there is an error while retrieving crash data from the database.
     */
    public String crashes() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("resetLayers();");

        crashData.getCrashLocations().stream().forEach(crash -> {
            HashMap crash1 = (HashMap) crash;
            double latitude = (double) crash1.get("latitude");
            double longitude = (double) crash1.get("longitude");
            int severity = (int) crash1.get("severity");
            String year = Integer.toString((Integer) crash1.get("crash_year"));
            String weather = (String) crash1.get("weather");

            stringBuilder.append(String.format("addPoint(%f,%f,%d,%s,'%s');", latitude, longitude, severity, year, weather));
        });

        stringBuilder.append("setHeatmapData();");

        System.out.println(stringBuilder);

        return stringBuilder.toString();
    }

    /**
     * Represents crash information containing latitude and longitude.
     */
    protected static class CrashInfo {
        /**
         * The latitude of the crash location.
         */
        public double lat;
        /**
         * The longitude of the crash location.
         */
        public double lng;
        public int severity;

        public String crashYear; // Add year
        public String weather;

        /**
         * Constructs a CrashInfo object with latitude and longitude.
         *
         * @param lat The latitude of the crash location.
         * @param lng The longitude of the crash location.
         */
        public CrashInfo(double lat, double lng, int severity, String year, String weather) {
            this.lat = lat;
            this.lng = lng;
            this.severity = severity;
            this.crashYear = year;
            this.weather = weather;

        }

    }

    /**
     * Retrieves the name of the current view in the application.
     *
     * @return The name of the current view.
     */
    public String currentView() {
        return SettingsMenuController.currentView;
    }



    /**
     * Sets the viewport variables in the FilterManager singleton class.
     *
     * @param minLatitude minimum latitude of the map view
     * @param minLongitude minimum longitude of the map view
     * @param maxLatitude maximum latitude of the map view
     * @param maxLongitude maximum longitude of the map view
     */
    public void setFilterManagerViewport(double minLatitude, double minLongitude,
                                         double maxLatitude, double maxLongitude) {
        FilterManager filterManager = FilterManager.getInstance();
        filterManager.setViewPortMin(minLatitude, minLongitude);
        filterManager.setViewPortMax(maxLatitude, maxLongitude);
    }

    /**
     * Sets the viewport variables in the FilterManager singleton class.
     *
     * @param minLatitude minimum latitude of the map view
     * @param minLongitude minimum longitude of the map view
     * @param maxLatitude maximum latitude of the map view
     * @param maxLongitude maximum longitude of the map view
     */
    public void setRatingAreaManagerBoundingBox(double minLatitude, double minLongitude,
                                         double maxLatitude, double maxLongitude) {
        RatingAreaManager ratingAreaManager = RatingAreaManager.getInstance();
        ratingAreaManager.setBoundingBoxMin(minLatitude, minLongitude);
        ratingAreaManager.setBoundingBoxMax(maxLatitude, maxLongitude);
    }

    /**
     * Calls mapLoaded function in the MainController class.
     */
    public void mapLoaded() {
        if (listener != null) {
            listener.mapLoaded();
        }
    }

    /**
     * Creates an interface for the listener to call relevant methods.
     */
    public static interface JavaScriptListener {
        void mapLoaded();
    }

    public void printTime(double time) {System.out.println(time);};
}

