package seng202.team10.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.tools.javac.Main;
import javafx.scene.control.Alert;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng202.team10.business.CrashManager;
import seng202.team10.business.FilterManager;
import seng202.team10.business.RatingAreaManager;
import seng202.team10.business.SettingsManager;
import seng202.team10.gui.MainController;
import seng202.team10.gui.RoutingMenuController;
import seng202.team10.gui.SettingsMenuController;



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
    private String currentView;
    private static Map<Long, List<Location>> routeMap = new ConcurrentHashMap<>();
    private static Map<Long, List<Double>> distancesMap = new ConcurrentHashMap<>();
    private static Map<Long, List<String>> roadsMap = new ConcurrentHashMap<>();
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
     */
    public void setCrashes() {
        CrashManager crashData = new CrashManager();
        List<?> crashList = crashData.getCrashLocations();
        updateCrashesByJavascript(crashList);
    }

    public static void updateCrashesByJavascript(List<?> crashList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Promise.resolve().then(function () {resetLayers();}).then(function () {");

        crashList.stream().forEach(crash -> {
            HashMap crash1 = (HashMap) crash;
            double latitude = (double) crash1.get("latitude");
            double longitude = (double) crash1.get("longitude");
            int severity = (int) crash1.get("severity");
            String year = Integer.toString((Integer) crash1.get("crash_year"));
            String weather = (String) crash1.get("weather");

            stringBuilder.append(String.format("addPoint(%f,%f,%d,%s,'%s');",
                    latitude, longitude, severity, year, weather));
        });

        stringBuilder.append("}).then(function () {showLayers();});");
        MainController.javaScriptConnector.call("runDataUpdate", stringBuilder.toString());
    }

    /**
     * Retrieves the name of the current view in the application.
     *
     * @return The name of the current view.
     */
    public String currentView() {
        return SettingsManager.getInstance().getCurrentView();
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

    /**
     * Processes and sends the coordinates received in a JSON format.
     * This method parses the provided JSON string to extract routeId and
     * a list of coordinates, and subsequently performs necessary operations
     * such as updating ratings or processing routes.
     *
     * @param coordinatesJson A JSON formatted string containing a routeId and
     *                        an array of coordinate objects with latitude and longitude values.
     *
     * @throws RuntimeException if there's an SQL exception during processing.
     */
    public void sendCoordinates(String coordinatesJson) {
        JSONParser parser = new JSONParser();
        try {
            // Parse the JSON string to a JSONObject
            JSONObject routeObj = (JSONObject) parser.parse(coordinatesJson);
            List<String> roads = new ArrayList<>();


            // Extract routeId, coordinates, instructionsRoads and instructionDistance
            long routeId = (long) routeObj.get("routeId");
            JSONArray jsonArray = (JSONArray) routeObj.get("coordinates");
            JSONArray jsonArray1 = (JSONArray) routeObj.get("instructionRoads");
            JSONArray jsonArray2 = (JSONArray) routeObj.get("instructionDistance");




            // Create a List to hold Coordinate objects
            List<Location> coordinates = new ArrayList<>();
            for (Object ajsonArray : jsonArray) {
                // Cast each item in the array to a JSONObject
                JSONObject coordJson = (JSONObject) ajsonArray;
                // Extract latitude and longitude from the JSONObject
                Object lat = (Object) coordJson.get("lat");
                Object lng = (Object) coordJson.get("lng");
                double finallat = 0;
                double finallng = 0;
                if (lat instanceof Long latLong) {
                    finallat = (latLong).doubleValue();
                } else if (lat instanceof Double latDouble) {
                    finallat = latDouble;
                }
                if (lng instanceof Long lngLong) {
                    finallng = lngLong.doubleValue();
                } else if (lng instanceof Double lngDouble) {
                    finallng = lngDouble;
                }

                // Add a new Coordinate object to the list
                coordinates.add(new Location(finallat, finallng));
            }


            List<Double> distances = new ArrayList<>();
            //since jsonArray1 and jsonArray2 are always the same size
            // both operations are in the same loop
            for (int i = 0; i < jsonArray2.size(); i++) {
                double output;
                Object distance = jsonArray2.get(i);
                if (distance instanceof Long distanceLong) {
                    //if distance is a Long convert it to a double
                    output = distanceLong.doubleValue();
                } else if (distance instanceof Double distanceDouble) { //already a double
                    output = distanceDouble;
                } else {
                    throw new IllegalArgumentException("Value is not a long");
                }
                String road = (String) jsonArray1.get(i);
                //add both items to the appropriate arrays
                roads.add(road);
                distances.add(output);
            }

            index = routeId; //store the routeId globally
            //store all of the arrays in a map with the routeIds
            processRoads(routeId, roads);
            processRoute(routeId, coordinates);
            processDistances(routeId, distances);
            //call the ratingupdate method in routingmenucontroller
            RoutingMenuController.ratingUpdate();

        } catch (Throwable e) {
            // Handle JSON parsing exceptions
            System.out.println(e);
        }
    }

    public static long getIndex() {
        return index;
    }


    private void processRoute(long routeId, List<Location> coordinates) {
        routeMap.put(routeId, coordinates);

    }

    private void processDistances(long routeId, List<Double> coordinates) {
        distancesMap.put(routeId, coordinates);
    }

    private void processRoads(long routeId, List<String> coordinates) {
        roadsMap.put(routeId, coordinates);
    }

    public static Map<Long, List<Location>> getRouteMap() throws SQLException {
        return routeMap;
    }



    public static Map<Long, List<Double>> getDistancesMap() {
        return distancesMap;
    }

    public static Map<Long, List<String>> getRoadsMap() {
        return roadsMap;
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
    public void setRatingAreaManagerBoundingCircle(double latitude, double longitude,
                                                   double radius) {
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

    public void printTime(String time) {
        System.out.println(time);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void enableRefreshButton() {
        mainController.enableRefresh();
    }
}

