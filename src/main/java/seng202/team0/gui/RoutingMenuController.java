package seng202.team0.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seng202.team0.business.FilterManager;
import seng202.team0.models.*;
import seng202.team0.repository.FavouriteDAO;
import seng202.team0.repository.SQLiteQueryBuilder;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import static seng202.team0.models.AngleFilter.filterLocationsByAngle;

public class RoutingMenuController implements Initializable {

    @FXML
    private TextField startLocation;
    @FXML
    private TextField endLocation;
    @FXML
    private TextField stopLocation;
    @FXML
    private Label numCrashesLabel;
    @FXML
    private ComboBox<String> loadRoutesComboBox;
    @FXML
    private Label ratingText;

    private GeoLocator geolocator;
    private List<Location> stops = new ArrayList<>();

    private static List<Location> matchedPoints;
    public static RoutingMenuController controller;

    /**
     * Overriding initialize of Routing Menu display.
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resourceBundle
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        geolocator = new GeoLocator();

        loadRoutesComboBox.setOnAction((ActionEvent event) -> {
            Object selectedItem = loadRoutesComboBox.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                System.out.println("Selected: " + selectedItem);
                try {
                    loadRoute();  // Assuming this method uses the selected item
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        controller = this;
    }

    /**
     * Used to display route on Leaflet Map in javascript code.
     * @param routes route to display
     */
    private void displayRoute(Route... routes) throws SQLException {
        List<Route> routesList = new ArrayList<>();
        Collections.addAll(routesList, routes);
        MainController.javaScriptConnector.call("displayRoute", Route.routesToJSONArray(routesList));
    }

    /**
     * Adds a location when the "Add Location" button is clicked.
     * Uses Geolocator class to turn the address into a lat, lng pair
     * @return Location object of start location.
     */

    @FXML
    private Location getStart() {
        String address = startLocation.getText().trim();
        if (address.isEmpty()) {
            return null;
        }
        Location newMarker = geolocator.getLocation(address);
        //javaScriptConnector.call("addMarker", address, newMarker.lat, newMarker.lng);
        return newMarker;
    }

    /**
     * Adds a stop when the "Add Stop" button is clicked.
     * @return Location object of stop location.
     */
    @FXML
    private Location getStop() {
        String address = stopLocation.getText().trim();
        if (address.isEmpty()) {
            return null;
        }
        Location newMarker = geolocator.getLocation(address);
        //javaScriptConnector.call("addMarker", address, newMarker.lat, newMarker.lng);
        return newMarker;
    }

    /**
     * Adds a location from the end destination text field.
     * @return Location object of end location.
     */
    @FXML
    private Location getEnd() {
        String address = endLocation.getText().trim();
        if (address.isEmpty()) {
            return null;
        }
        Location newMarker = geolocator.getLocation(address);
        //javaScriptConnector.call("addMarker", address, newMarker.lat, newMarker.lng);
        return newMarker;
    }

    /**
     * Callback event for "Save Route" button.
     */
    @FXML
    private void saveRoute() {
        Location start = getStart();
        Location end = getEnd();
        String filters = FilterManager.getInstance().toString();
        String startAddress = geolocator.getAddress(start.latitude, start.longitude);
        String endAddress = geolocator.getAddress(end.latitude, end.longitude);
        Favourite favourite = new Favourite(startAddress, endAddress, start.latitude, start.longitude, end.latitude, end.longitude, filters);
        FavouriteDAO favorites = new FavouriteDAO();
        favorites.addOne(favourite);
    }


    /**
     * Callback event for displaying events in favourites combo box.
     */
    @FXML
    private void displayRoutes() {
        FavouriteDAO favourites = new FavouriteDAO();
        List<Favourite> favouritesList = favourites.getAll();
        ObservableList<String> items = FXCollections.observableArrayList(favouritesList.stream().map(favourite -> {return favourite.getStartAddress()+" to "+favourite.getEndAddress();}).toList());
        loadRoutesComboBox.setItems(items);
    }

    /**
     * Listener function for when an option in the favourites combo box is selected.
     * @throws SQLException exception for if an SQLite query goes wrong
     */
    @FXML
    private void loadRoute() throws SQLException {
        int favouriteID = loadRoutesComboBox.getSelectionModel().getSelectedIndex()+1;
        System.out.println(favouriteID);
        if (favouriteID != 0 && favouriteID != -1) {
            FavouriteDAO favourites = new FavouriteDAO();
            Favourite favourite = favourites.getOne(favouriteID);

            // Update FilterManager class with the filters associated to the favourite route and Checkboxes to match
            FilterManager filters = FilterManager.getInstance();
            filters.updateFiltersWithQueryString(favourite.getFilters());

            // Generates a route and makes sure stops is cleared
            stops.clear();
            generateRouteAction(favourite);

            startLocation.setText(favourite.getStartAddress());
            endLocation.setText(favourite.getEndAddress());
        }
    }

    /**
     * Adds stop to "stops" list.
     */
    @FXML
    private void addStop() throws SQLException {
        Location stop = getStop();
        if (stop != null) {
            stops.add(stop);
        }
        generateRouteAction();
    }

    /**
     * Removes stop from "stops" list
     */
    @FXML
    private void removeStop() throws SQLException {
        if (stops.size() >= 1) {
            stops.remove(stops.size()-1);
            generateRouteAction();
        }
    }

    /**
     * Retrieves crash points that overlap with a given route within a specified danger radius.
     * This method retrieves crash information from a database, calculates distances between the route
     * segments and crash points using the Haversine formula, and identifies crash points that fall
     * within the specified danger radius of the route segments.
     *
     * @return A list of CrashInfo objects representing crash points that overlap with the route.
     */
    public static double getOverlappingPoints(List<Location> coordinates) {
        double totalValue = 0;
        double totalDistance = 0;
        for (int i = 0; i < coordinates.size()-1; i+=1) {
            Location segmentStart = coordinates.get(i);
            Location segmentEnd = coordinates.get(i+1);
            double distance = haversineDistance(segmentStart, segmentEnd);
            double averageSeverity = crossProductQuery(segmentStart, segmentEnd);
            totalDistance += distance;
            totalValue += averageSeverity / totalDistance;
        }
        //TODO store average severities in a list to look at coloring route segments.
        return totalValue * totalDistance;
    }

    /**
     * Calculates the Haversine distance between two geographic coordinates using the Haversine formula.
     * The Haversine formula is used to compute the distance between two points on the Earth's surface
     * given their latitude and longitude coordinates.
     *
     * @param loc1 The first location with latitude and longitude coordinates.
     * @param loc2 The second location with latitude and longitude coordinates.
     * @return The Haversine distance between the two locations in meters.
     */
    public static double haversineDistance(Location loc1, Location loc2) {
        double R = 6371000; // Earth radius in meters
        double dLat = Math.toRadians(loc2.latitude - loc1.latitude);
        double dLon = Math.toRadians(loc2.longitude - loc1.longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(loc1.latitude)) * Math.cos(Math.toRadians(loc2.latitude))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }



    /**
     * Takes in two locations of a start and end location and queries the database
     * for an average severity of crashes within a 1km radius along the line between
     * the two locations
     * @param startLocation location the route segment starts at
     * @param endLocation location the route segment ends at
     * @return double of average severity
     */
    public static double crossProductQuery(Location startLocation, Location endLocation) {
        double start_long_rad = Math.toRadians(startLocation.longitude);
        double start_lat_rad = Math.toRadians(startLocation.latitude);
        double end_long_rad = Math.toRadians(endLocation.longitude);
        double end_lat_rad = Math.toRadians(endLocation.latitude);

        double start_x = Math.cos(start_lat_rad) * Math.cos(start_long_rad);
        double start_y = Math.cos(start_lat_rad) * Math.sin(start_long_rad);
        double start_z = Math.sin(start_lat_rad);

        double end_x = Math.cos(end_lat_rad) * Math.cos(end_long_rad);
        double end_y = Math.cos(end_lat_rad) * Math.sin(end_long_rad);
        double end_z = Math.sin(end_lat_rad);

        Map<String, Number> constantsTable = new LinkedHashMap<>();
        constantsTable.put("start_x", start_x);
        constantsTable.put("start_y", start_y);
        constantsTable.put("start_z", start_z);
        constantsTable.put("end_x", end_x);
        constantsTable.put("end_y", end_y);
        constantsTable.put("end_z", end_z);

        double minLon = Math.min(startLocation.longitude, endLocation.longitude);
        double maxLon = Math.max(startLocation.longitude, endLocation.longitude);
        double minLat = Math.min(startLocation.latitude, endLocation.latitude);
        double maxLat = Math.max(startLocation.latitude, endLocation.latitude);

        // One kilometre in degrees
        double oneKilometreInDegrees = 0.008;
        double distance = 100;

        String tableName = "locations";
        String crossProductMagnitude = "SQRT(POWER((COS(RADIANS(latitude)) * SIN(RADIANS(longitude)) - "
                                + tableName + ".start_y) * (" + tableName + ".end_z - " + tableName + ".start_z) - (SIN(RADIANS(latitude)) - "
                                + tableName + ".start_z) * (" + tableName + ".end_y - " + tableName + ".start_y), 2) + POWER((SIN(RADIANS(latitude)) - "
                                + tableName + ".start_z) * (" + tableName + ".end_x - " + tableName + ".start_x) - (COS(RADIANS(latitude)) * COS(RADIANS(longitude)) - "
                                + tableName + ".start_x) * (" + tableName + ".end_z - " + tableName + ".start_z), 2) + POWER((COS(RADIANS(latitude)) * COS(RADIANS(longitude)) - "
                                + tableName + ".start_x) * (" + tableName + ".end_y - " + tableName + ".start_y) - (COS(RADIANS(latitude)) * SIN(RADIANS(longitude)) - "
                                + tableName + ".start_y) * (" + tableName + ".end_x - " + tableName + ".start_x), 2))";
        String lineMagnitude = "SQRT(POWER(" + tableName + ".end_x - " + tableName + ".start_x, 2) + POWER("
                                + tableName + ".end_y - " + tableName + ".start_y, 2) + POWER("
                                + tableName + ".end_z - " + tableName + ".start_z, 2))";
        String aSinTheta = "(ASIN(" + crossProductMagnitude + "/" + lineMagnitude + ")";
        String worldDistance = aSinTheta + " * 6371.0) <= " + distance;

        FilterManager filterManager = FilterManager.getInstance();
        Location previousMin = filterManager.getViewPortMin();
        Location previousMax = filterManager.getViewPortMax();

        filterManager.setViewPortMin(new Location(minLat - oneKilometreInDegrees, minLon - oneKilometreInDegrees));
        filterManager.setViewPortMax(new Location(maxLat + oneKilometreInDegrees, maxLon + oneKilometreInDegrees));

        String filterWhere = filterManager.toString();

        filterManager.setViewPortMin(previousMin);
        filterManager.setViewPortMax(previousMax);

        String select = "severity";
        String from = "crashes, locations";
        String where = filterWhere + " AND " + worldDistance;

        List severityList = SQLiteQueryBuilder.create()
                            .with(tableName, constantsTable)
                            .select(select)
                            .from(from)
                            .where(where)
                            .build();
        int totalSeverity = 0;
        double total = 0;

        for (Object severityMap : severityList) {
             HashMap<String, Object> map = (HashMap<String, Object>) severityMap;
             totalSeverity += (int) map.get("severity");
             total += 1;
        }

        if (total > 0) {
            return totalSeverity / total;
        } else {
            return 0;
        }

    }

    /**
     * OnAction event callback function for "Generate Route" button
     */
    @FXML
    private void generateRouteAction() throws SQLException {
        Location start = getStart();
        Location end = getEnd();

        if (start != null && end != null) {
            List<Location> routeLocations = new ArrayList<>();
            routeLocations.add(start);
            routeLocations.addAll(stops);  // add all the stops
            routeLocations.add(end);

            Route route = new Route(List.of(routeLocations.toArray(new Location[0])));
            displayRoute(route);
        }
    }

    /**
     * Updates the ratingText label's text to the rating provided.
     * @param rating string of numeric rating.
     */
    public void updateRatingLabel(String rating) {
        ratingText.setText("Rating: "+ rating);
    }

    /**
     * Takes the list of coordinates stored in JavaScriptBridge and updates the rating shown
     * on the GUI's ratingText label through getting the overlapping points of each segment.
     * @throws SQLException
     */
    public static void ratingUpdate() throws SQLException {
        List<Location> coordinates = JavaScriptBridge.getRouteMap().get(JavaScriptBridge.getIndex()); // Assuming '0' is the routeId you are interested in
        if(coordinates != null && !coordinates.isEmpty()) { // Null and empty check to prevent NullPointerException
            double rating = getOverlappingPoints(coordinates); // Calculate rating based on coordinates
            RoutingMenuController.controller.updateRatingLabel(Double.toString(Math.round(rating))); // Update the UI
        } else {
            System.out.println("No coordinates available for routeId: 0");
        }
    }


    /**
     * Overloaded function for handling route generation with favourites.
     * @param favourite Favourite object with locations of route and filters.
     */
    private void generateRouteAction(Favourite favourite) throws SQLException {
        Location start = new Location(favourite.getStartLat(), favourite.getStartLong());
        Location end = new Location(favourite.getEndLat(), favourite.getEndLong());

        if (start != null && end != null) {
            List<Location> routeLocations = new ArrayList<>();
            routeLocations.add(start);
            routeLocations.addAll(stops);
            routeLocations.add(end);

            Route route = new Route(List.of(routeLocations.toArray(new Location[0])));
            displayRoute(route);

        }
    }

}
