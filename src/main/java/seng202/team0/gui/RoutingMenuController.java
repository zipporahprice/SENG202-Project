package seng202.team0.gui;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.util.Pair;
import org.controlsfx.control.PopOver;
import seng202.team0.business.CrashManager;
import seng202.team0.business.FilterManager;
import seng202.team0.business.RouteManager;
import seng202.team0.models.Crash;
import seng202.team0.models.Favourite;
import seng202.team0.models.GeoLocator;
import seng202.team0.models.Location;
import seng202.team0.models.Route;
import seng202.team0.repository.SqliteQueryBuilder;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import static seng202.team0.models.AngleFilter.filterLocationsByAngle;


/**
 * The `RoutingMenuController` class manages user
 * interactions related to routing and displaying routes
 * in our application. It implements the `Initializable`
 * and `MenuController` interfaces for initialization
 * and updating of routing settings. This class allows
 * users to input start, end, and stop locations for route
 * generation, select a transport mode, and view route
 * information, including the number of crashes along the route
 * and a danger rating.
 *
 * @author Team 10
 */
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
    @FXML
    private Button carButton;
    @FXML
    private Button bikeButton;
    @FXML
    private Button walkingButton;

    @FXML Button generateRoute;

    @FXML
    private Button removeRoute;


    private static List<Location> matchedPoints;
    public static RoutingMenuController controller;
    private GeoLocator geolocator;
    private List<Location> stops = new ArrayList<>();
    private Button selectedButton = null;
    private String modeChoice;

    private PopOver popOver;


    /**
     * Initializes the JavaFX controller when the associated FXML file is loaded.
     * Creates an instance of the GeoLocater Class which is used to
     * find the locations and create the routes

     * @param url            The location of the FXML file.
     * @param resourceBundle The resource bundle.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        geolocator = new GeoLocator();
        carButton.setUserData("car");
        bikeButton.setUserData("bike");
        walkingButton.setUserData("walking");
        removeRoute.setDisable(true);



        loadRoutesComboBox.setOnAction((ActionEvent event) -> {
            Object selectedItem = loadRoutesComboBox.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                try {
                    loadRoute();  // Assuming this method uses the selected item
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        controller = this;
        loadManager();
    }


    /**
     * Displays a notification message near the specified button when it is pressed.
     *
     * @param btn     The button for which the notification is displayed.
     * @param message The message to be displayed in the notification.
     */
    private void showNotificationOnButtonPress(Button btn, String message) {
        if (popOver != null && popOver.isShowing()) {
            popOver.hide();
        }
        Label label = new Label(message);
        label.setFont(new Font(20.0));
        label.setPadding(new Insets(5));
        popOver = new PopOver(label);
        popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
        popOver.show(walkingButton);


        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5),
                popOver.getSkin().getNode());

        fadeOut.setDelay(Duration.millis(1500));
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> popOver.hide());
        fadeOut.play();



    }

    /**
     * Displays a route or routes based on safety score, mode choice, and an array of routes.
     *
     * @param safetyScore The safety score associated with the route.
     * @param routes      An array of Route objects to display.
     */
    private void displayRoute(int safetyScore, Route... routes) {
        List<Route> routesList = new ArrayList<>();
        Collections.addAll(routesList, routes);
        if (modeChoice == null) {
            showNotificationOnButtonPress(generateRoute, "Please select a transport option");
        } else {
            MainController.javaScriptConnector.call("displayRoute", Route
                    .routesToJsonArray(routesList), modeChoice, safetyScore);
        }
    }

    /**
     * Displays a popover near a TextField with a specified message and fade-out duration.
     *
     * @param message   The message to be displayed in the popover.
     * @param textField The TextField near which the popover should be displayed.
     * @param time      The duration (in seconds) for the fade-out animation.
     */
    private void showPopOver(String message, TextField textField, double time) {
        Label label = new Label(message);
        popOver = new PopOver(label);
        popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
        popOver.show(textField);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(time),
                popOver.getSkin().getNode());

        fadeOut.setDelay(Duration.millis(1500));
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> popOver.hide());
        fadeOut.play();

    }


    /**
     * Retrieves the start location based on user input from a TextField.
     *
     * @return The start Location object, or null if the input is empty or invalid.
     */
    @FXML
    private Location getStart() {
        String address = startLocation.getText().trim();
        if (address.isEmpty()) {
            return null;
        }
        Pair<Location, String> startResult = geolocator.getLocation(address);
        Location startMarker = startResult.getKey();
        String errorMessageStart = startResult.getValue();
        if (errorMessageStart != null) {
            showPopOver(errorMessageStart, startLocation, 5);
            return null; // You might want to return null here if there was an error.
        }
        // e.g., javaScriptConnector.call("addMarker", address, newMarker.lat, newMarker.lng);
        return startMarker;
    }

    /**
     * Retrieves the end location based on user input from a TextField.
     *
     * @return The end Location object, or null if the input is empty or invalid.
     */
    @FXML
    private Location getEnd() {
        String address = endLocation.getText().trim();
        if (address.isEmpty()) {
            return null;
        }
        Pair<Location, String> endResult = geolocator.getLocation(address);
        Location newMarker = endResult.getKey();
        String errorMessage = endResult.getValue();
        if (errorMessage != null) {
            showPopOver(errorMessage, endLocation, 5);
            return null; // You might want to return null here if there was an error.
        }
        // e.g., javaScriptConnector.call("addMarker", address, newMarker.lat, newMarker.lng);
        return newMarker;
    }

    /**
     * Saves a route or favorite location to a database.
     *
     * @throws SQLException If a database error occurs during the save operation.
     */
    @FXML
    private void saveRoute() {
        Location start = getStart();
        Location end = getEnd();
        String filters = FilterManager.getInstance().toString();
        String startAddress = geolocator.getAddress(start.getLatitude(),
                start.getLongitude(), "Start");
        String endAddress = geolocator.getAddress(end.getLatitude(), end.getLongitude(), "End");
        Favourite favourite = new Favourite(startAddress, endAddress,
                start.getLatitude(), start.getLongitude(), end.getLatitude(),
                end.getLongitude(), filters);

        List<Favourite> favourites = new ArrayList<>();
        favourites.add(favourite);

        SqliteQueryBuilder.create().insert("favourites").buildSetter(favourites);
    }


    /**
     * Populates the ComboBox with a list of saved routes or favorite locations.
     */
    @FXML
    private void displayRoutes() {
        List<?> favouritesList = SqliteQueryBuilder.create()
                                                    .select("*")
                                                    .from("favourites")
                                                    .buildGetter();
        ObservableList<String> items = FXCollections.observableArrayList(favouritesList
                .stream().map(favourite -> {
                    Favourite favouriteCasted = (Favourite) favourite;
                    return favouriteCasted.getStartAddress()
                        + " to " + favouriteCasted.getEndAddress(); }).toList());
        loadRoutesComboBox.setItems(items);
    }

    /**
     * Loads a selected route or favorite location from the ComboBox.
     *
     * @throws SQLException If a database error occurs during the loading operation.
     */
    @FXML
    private void loadRoute() throws SQLException {
        int favouriteId = loadRoutesComboBox.getSelectionModel().getSelectedIndex() + 1;
        if (favouriteId != 0 && favouriteId != -1) {
            List<?> favouriteList = SqliteQueryBuilder.create()
                                                        .select("*")
                                                        .from("favourites")
                                                        .where("id = " + favouriteId)
                                                        .buildGetter();

            Favourite favourite = (Favourite) favouriteList.get(0);

            // Update FilterManager class with the filters associated to the favourite route
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
     * Retrieves a stop location based on user input from a TextField.
     *
     * @return The stop Location object, or null if the input is empty or invalid.
     */

    @FXML
    private Location getStop() {
        String address = stopLocation.getText().trim();
        if (address.isEmpty()) {
            return null;
        }
        Pair<Location, String> endResult = geolocator.getLocation(address);
        Location newMarker = endResult.getKey();
        String errorMessage = endResult.getValue();
        if (errorMessage != null) {
            showPopOver(errorMessage, stopLocation, 5);
            return null; // You might want to return null here if there was an error.
        }
        // e.g., javaScriptConnector.call("addMarker", address, newMarker.lat, newMarker.lng);
        return newMarker;
    }

    /**
     * Adds a stop location to the collection and generates a route action.
     *
     * @throws SQLException If a database error occurs during the operation.
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
     * Removes the last stop from the collection and generates a route.
     *
     * @throws SQLException If a database error occurs during the route generation.
     */
    @FXML
    private void removeStop() throws SQLException {
        if (stops.size() >= 1) {
            stops.remove(stops.size() - 1);
            generateRouteAction();
        }
    }

    /**
     * Retrieves crash points that overlap with a given route within a specified danger radius.
     * This method retrieves crash information from a database,
     * calculates distances between the route
     * segments and crash points using the Haversine formula,
     * and identifies crash points that fall
     * within the specified danger radius of the route segments.
     *
     * @param route        The route for which overlapping crash points are to be identified.
     * @param dangerRadius The radius in which crashes are considered overlapping with the route.
     * @return A list of CrashInfo objects representing crash points that overlap with the route.
     * @throws SQLException If an SQL-related error occurs during database query execution.
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
     * Calculates the Haversine distance between two geographic
     * coordinates using the Haversine formula.
     * The Haversine formula is used to compute
     * the distance between two points on the Earth's surface
     * given their latitude and longitude coordinates.
     *
     * @param loc1 The first location with latitude and longitude coordinates.
     * @param loc2 The second location with latitude and longitude coordinates.
     * @return The Haversine distance between the two locations in meters.
     */

    public double haversineDistance(Location loc1, Crash loc2) {
        double r = 6371000; // Earth radius in meters
        double deltaLat = Math.toRadians(loc2.getLatitude() - loc1.getLatitude());
        double deltaLon = Math.toRadians(loc2.getLongitude() - loc1.getLongitude());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(Math.toRadians(loc1.getLatitude()))
                * Math.cos(Math.toRadians(loc2.getLatitude()))
                * Math.sin(deltaLon  / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return r * c;
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
     * Generates a route, calculates its danger rating, and updates the UI.
     *
     * @throws SQLException If a database error occurs during the process.
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
            List<Crash> crashInfos = getOverlappingPoints(route, 1000);
            int total = ratingGenerator(crashInfos);
            ratingText.setText("Danger: " + total + "/5");
            numCrashesLabel.setText("Number of crashes on route: " + crashInfos.size());
            displayRoute(total, route);
            removeRoute.setDisable(false);
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
            List<Crash> crashInfos = getOverlappingPoints(route, 1000);
            int total = ratingGenerator(crashInfos);
            displayRoute(total, route);
            ratingText.setText("Danger: " + total + "/5");
            numCrashesLabel.setText("Number of crashes on route: " + crashInfos.size());
        }
    }

    /**
     * Calls the JS function, removeRoute.
     * When the corresponding button is pressed in the
     * GUI, this method is called and
     * the route is removed
     */
    @FXML
    private void removeRoute() {
        MainController.javaScriptConnector.call("removeRoute");
        startLocation.setText("");
        endLocation.setText("");
        modeChoice = null;
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("clickedButtonColor");
            selectedButton.getStyleClass().add("hamburgerStyle");
        }

        removeRoute.setDisable(true);

    }


    /**
     * Calculates a danger rating based on information about crashes.
     *
     * @param crashInfos A list of Crash objects containing crash information.
     * @return The danger rating as an integer.
     */
    private int ratingGenerator(List<Crash> crashInfos) {
        int total = 0;
        for (Crash crash : crashInfos) {
            total += crash.getSevereInt();
        }
        if (crashInfos.size() == 0) {
            total = (total * 10) - 10;
        } else {
            total = (total * 10 / crashInfos.size()) - 10;
        }
        if (total > 5) {
            total = 5;
        } else if (total < 0) {
            total = 0;
        }
        return total;
    }




    /**
     * Toggles between the transport mode buttons.
     * When one button is selected, the previously selected button (if any) is deselected.
     *
     * @param event An ActionEvent called when the button is pressed
     */
    public void toggleModeButton(ActionEvent event) {
        Button chosenButton = (Button) event.getSource();

        modeChoice = (String) chosenButton.getUserData();

        if (Objects.equals(chosenButton, selectedButton)) {
            modeChoice = null;
            selectedButton = null;
            chosenButton.getStyleClass().remove("clickedButtonColor");
            chosenButton.getStyleClass().add("hamburgerStyle");
        } else if (!Objects.equals(chosenButton, selectedButton) && selectedButton != null) {
            selectedButton.getStyleClass().remove("clickedButtonColor");
            selectedButton.getStyleClass().add("hamburgerStyle");
            selectedButton = chosenButton;
            chosenButton.getStyleClass().remove("hamburgerStyle");
            chosenButton.getStyleClass().add("clickedButtonColor");
            modeChoice = (String) chosenButton.getUserData();

        } else {
            selectedButton = chosenButton;
            chosenButton.getStyleClass().remove("hamburgerStyle");
            chosenButton.getStyleClass().add("clickedButtonColor");
            modeChoice = (String) chosenButton.getUserData();

        }
    }

    /**
     * Loads the route data stored from the RouteManager into the routing menu.
     */
    @Override
    public void loadManager() {
        RouteManager route = RouteManager.getInstance();

        // retrieve all updated location data
        String startLoc = route.getStartLocation();
        String endLoc = route.getEndLocation();
        String stopLoc = route.getStopLocation();

        // update textFields according to data
        startLocation.setText(startLoc);
        endLocation.setText(endLoc);
        stopLocation.setText(stopLoc);
    }

    /**
     * Updates the RouteManager's stored data with data currently in the routing menu.
     */
    @Override
    public void updateManager() {
        RouteManager route = RouteManager.getInstance();
        route.setStartLocation(startLocation.getText());
        route.setEndLocation(endLocation.getText());
        route.setStopLocation(stopLocation.getText());
    }



}
