package seng202.team0.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng202.team0.business.CrashManager;
import seng202.team0.business.FilterManager;
import seng202.team0.business.RatingAreaManager;
import seng202.team0.gui.MainController;
import seng202.team0.gui.RoutingMenuController;
import seng202.team0.gui.SettingsMenuController;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


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
    private static Map<Long, List<Location>> routeMap = new ConcurrentHashMap<>();
    private static long index;

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

            stringBuilder.append(String.format("addPoint(%f,%f,%d,%s,'%s');",
                    latitude, longitude, severity, year, weather));
        });

        stringBuilder.append("setHeatmapData();");

        return stringBuilder.toString();
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

    public void printOutput(Object string1) {
        System.out.println(string1);
    }

    public void sendCoordinates(String coordinatesJson) {
        JSONParser parser = new JSONParser();
        try {
            // Parse the JSON string to a JSONObject
            JSONObject routeObj = (JSONObject) parser.parse(coordinatesJson);

            // Extract routeId and coordinates
            long routeId = (long) routeObj.get("routeId");
            JSONArray jsonArray = (JSONArray) routeObj.get("coordinates");

            // Create a List to hold Coordinate objects
            List<Location> coordinates = new ArrayList<>();

            // Iterate over the items in the JSONArray
            for (Object aJsonArray : jsonArray) {
                // Cast each item in the array to a JSONObject
                JSONObject coordJson = (JSONObject) aJsonArray;

                // Extract latitude and longitude from the JSONObject
                double lat = (double) coordJson.get("lat");
                double lng = (double) coordJson.get("lng");

                // Add a new Coordinate object to the list
                coordinates.add(new Location(lat, lng)); // Ensure you have a Coordinate class with a constructor that accepts lat and lng
            }
            index = routeId;
            processRoute(routeId, coordinates);
            RoutingMenuController.ratingUpdate();

            // Now you have a List of Coordinates in Java
            // Do something with the coordinates...

        } catch (ParseException e) {
            // Handle JSON parsing exceptions
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static long getIndex() {
        return index;
    }


    private void processRoute(long routeId, List<Location> coordinates) {
        // Example: You might store the coordinates in a Map where the key is the routeId
        routeMap.put(routeId, coordinates);

    }

    public static Map<Long, List<Location>> getRouteMap() throws SQLException {
        return routeMap;
    }

    /**
     * Sets the bounding box variables in the RatingAreaManager singleton class.
     * Clears the bounding circle variables.
     *
     * @param minLatitude minimum latitude of the bounding box
     * @param minLongitude minimum longitude of the bounding box
     * @param maxLatitude maximum latitude of the bounding box
     * @param maxLongitude maximum longitude of the bounding box
     */
    public void setRatingAreaManagerBoundingBox(double minLatitude, double minLongitude,
                                                double maxLatitude, double maxLongitude) {
        // Setting the Bounding Box
        RatingAreaManager ratingAreaManager = RatingAreaManager.getInstance();
        ratingAreaManager.setBoundingBoxMin(minLatitude, minLongitude);
        ratingAreaManager.setBoundingBoxMax(maxLatitude, maxLongitude);

        // Clearing the Bounding Circle
        ratingAreaManager.setBoundingCircleCentre(null, null);
        ratingAreaManager.setBoundingCircleRadius(0);
    }

    /**
     * Sets the bounding circle variables in the RatingAreaManager singleton class.
     * Clears the bounding box variables.
     *
     * @param latitude latitude of the bounding circle
     * @param longitude longitude of the bounding circle
     * @param radius radius of the bounding circle
     */
    public void setRatingAreaManagerBoundingCircle(double latitude, double longitude, double radius) {
        // Setting the Bounding Circle
        RatingAreaManager ratingAreaManager = RatingAreaManager.getInstance();
        ratingAreaManager.setBoundingCircleCentre(latitude, longitude);
        ratingAreaManager.setBoundingCircleRadius(radius);

        // Clearing the Bounding Box
        ratingAreaManager.setBoundingBoxMin(null, null);
        ratingAreaManager.setBoundingBoxMax(null, null);
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

    public void printTime(double time) {
        System.out.println(time);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void enableRefreshButton() {
        mainController.enableRefresh();
    }
}

