package seng202.team0.gui;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.util.Pair;
import org.controlsfx.control.PopOver;
import seng202.team0.business.FilterManager;
import seng202.team0.business.RouteManager;
import seng202.team0.models.Crash;
import seng202.team0.models.Favourite;
import seng202.team0.models.GeoLocator;
import seng202.team0.models.JavaScriptBridge;
import seng202.team0.models.Location;
import seng202.team0.models.Route;
import seng202.team0.repository.SqliteQueryBuilder;

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
public class RoutingMenuController implements Initializable, MenuController {

    @FXML
    private ComboBox<String> startLocation;
    @FXML
    private ComboBox<String> endLocation;
    @FXML
    private ComboBox<String> stopLocation;
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
    @FXML
    ListView<String> stopsListView = new ListView<>();


    private static List<Location> matchedPoints;
    public static RoutingMenuController controller;
    private GeoLocator geolocator;
    private List<Location> stops = new ArrayList<>();
    private Button selectedButton = null;
    private String modeChoice;

    private String startAddress;
    private String endAddress;
    private String stopAddress;

    private PopOver popOver;
    private final List<Button> transportButtons = new ArrayList<>();
    private ObservableList<String> stopStrings = FXCollections.observableArrayList();



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
        transportButtons.add(carButton);
        transportButtons.add(bikeButton);
        transportButtons.add(walkingButton);
        selectedButton = carButton;
        removeRoute.setDisable(true);
        stopsListView.setItems(stopStrings);
        stopsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//        stopsListView.setOnMouseClicked(e -> {
//            int selectedIndex = stopsListView.getSelectionModel().getSelectedIndex();
//            if (selectedIndex >= 0) {
//                stopsListView.getSelectionModel().clearSelection();
//            }
//        });

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
     * @param routes      An array of Route objects to display.
     */
    private void displayRoute(Route... routes) {
        List<Route> routesList = new ArrayList<>();
        Collections.addAll(routesList, routes);
        if (modeChoice == null) {
            showNotificationOnButtonPress(generateRoute, "Please select a transport option");
        } else {
            MainController.javaScriptConnector.call("displayRoute", Route
                    .routesToJsonArray(routesList), modeChoice);
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
        String address = startAddress;
        if (address.isEmpty()) {
            return null;
        }
        Pair<Location, String> startResult = geolocator.getLocation(address);
        return startResult.getKey();
    }

    @FXML
    private void setStart() {
        String selectedItem = startLocation.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            startAddress = selectedItem;
        }
    }

    @FXML
    private void loadStartOptions() {
        String address = startLocation.getEditor().getText().trim();
        ObservableList<String> addressOptions = geolocator.getAddressOptions(address);
        startLocation.setItems(addressOptions);
    }

    /**
     * Retrieves the end location based on user input from a TextField.
     *
     * @return The end Location object, or null if the input is empty or invalid.
     */
    @FXML
    private Location getEnd() {
        String address = endAddress;
        if (address.isEmpty()) {
            return null;
        }
        Pair<Location, String> endResult = geolocator.getLocation(address);
        return endResult.getKey();
    }

    @FXML
    private void setEnd() {
        String selectedItem = endLocation.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            endAddress = selectedItem;
        }
    }

    @FXML
    private void loadEndOptions() {
        String address = endLocation.getEditor().getText().trim();
        ObservableList<String> addressOptions = geolocator.getAddressOptions(address);
        endLocation.setItems(addressOptions);
    }

    @FXML
    private Location getStop() {
        String address = stopAddress;
        if (address.isEmpty()) {
            return null;
        }
        Pair<Location, String> stopResult = geolocator.getLocation(address);
        return stopResult.getKey();
    }

    @FXML
    private void setStop() {
        String selectedItem = stopLocation.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            stopAddress = selectedItem;
        }
    }

    @FXML
    private void loadStopOptions() {
        String address = stopLocation.getEditor().getText().trim();
        ObservableList<String> addressOptions = geolocator.getAddressOptions(address);
        stopLocation.setItems(addressOptions);
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
                end.getLongitude(), filters, modeChoice);

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

            startLocation.getEditor().setText(favourite.getStartAddress());
            endLocation.getEditor().setText(favourite.getEndAddress());
            for (Button button : transportButtons) {
                if (button.getUserData().equals(favourite.getTransportMode())) {
                    selectButton(button);
                }
            }
        }
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
            stopStrings.add(stopLocation.getValue());
            stopLocation.getEditor().setText(null);
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

            if (!stopsListView.getSelectionModel().getSelectedItem().isEmpty()) {
                String selectedStop = stopsListView.getSelectionModel().getSelectedItem();
                stopStrings.remove(selectedStop);
            } else {
                stopStrings.remove(-1);
            }

            //stopStrings.remove(stopLocation.getValue());
            generateRouteAction();
        }
    }

    /**
     * Gets the overlapping points from the list of
     * Location coordinates for rating the route.
     *
     * @param coordinates List of Location object coordinates
     * @return double of route rating
     */
    public static double getOverlappingPoints(List<Location> coordinates) {
        double totalValue = 0;
        double totalDistance = 0;
        for (int i = 0; i < coordinates.size() - 1; i += 1) {
            Location segmentStart = coordinates.get(i);
            Location segmentEnd = coordinates.get(i + 1);
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

    public static double haversineDistance(Location loc1, Location loc2) {
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
     * the two locations.
     *
     * @param startLocation location the route segment starts at
     * @param endLocation location the route segment ends at
     * @return double of average severity
     */
    public static double crossProductQuery(Location startLocation, Location endLocation) {
        double startLatRad = Math.toRadians(startLocation.getLatitude());
        double startLongRad = Math.toRadians(startLocation.getLongitude());
        double endLatRad = Math.toRadians(endLocation.getLatitude());
        double endLongRad = Math.toRadians(endLocation.getLongitude());


        double startX = Math.cos(startLatRad) * Math.cos(startLongRad);
        double startY = Math.cos(startLatRad) * Math.sin(startLongRad);
        double startZ = Math.sin(startLatRad);

        double endX = Math.cos(endLatRad) * Math.cos(endLongRad);
        double endY = Math.cos(endLatRad) * Math.sin(endLongRad);
        double endZ = Math.sin(endLatRad);

        Map<String, Number> constantsTable = new LinkedHashMap<>();
        constantsTable.put("start_x", startX);
        constantsTable.put("start_y", startY);
        constantsTable.put("start_z", startZ);
        constantsTable.put("end_x", endX);
        constantsTable.put("end_y", endY);
        constantsTable.put("end_z", endZ);

        double minLon = Math.min(startLocation.getLongitude(), endLocation.getLongitude());
        double maxLon = Math.max(startLocation.getLongitude(), endLocation.getLongitude());
        double minLat = Math.min(startLocation.getLatitude(), endLocation.getLatitude());
        double maxLat = Math.max(startLocation.getLatitude(), endLocation.getLatitude());

        // One kilometre in degrees
        double kilometreInDegrees = 0.008;
        double distance = 100;

        String tableName = "locations";
        String crossProductMagnitude = "SQRT(POWER((COS(RADIANS(latitude))*SIN(RADIANS(longitude))-"
                + tableName + ".start_y) * (" + tableName + ".end_z - "
                + tableName + ".start_z) - (SIN(RADIANS(latitude)) - "
                + tableName + ".start_z) * (" + tableName + ".end_y - "
                + tableName + ".start_y), 2) + POWER((SIN(RADIANS(latitude)) - "
                + tableName + ".start_z) * (" + tableName + ".end_x - "
                + tableName + ".start_x) - (COS(RADIANS(latitude)) * COS(RADIANS(longitude)) - "
                + tableName + ".start_x) * (" + tableName + ".end_z - "
                + tableName + ".start_z),2)+POWER((COS(RADIANS(latitude))*COS(RADIANS(longitude))-"
                + tableName + ".start_x) * (" + tableName + ".end_y - "
                + tableName + ".start_y) - (COS(RADIANS(latitude)) * SIN(RADIANS(longitude)) - "
                + tableName + ".start_y) * (" + tableName + ".end_x - "
                + tableName + ".start_x), 2))";
        String lineMagnitude = "SQRT(POWER(" + tableName + ".end_x - " + tableName
                + ".start_x, 2) + POWER(" + tableName + ".end_y - " + tableName
                + ".start_y, 2) + POWER(" + tableName + ".end_z - " + tableName
                + ".start_z, 2))";
        String asinTheta = "(ASIN(" + crossProductMagnitude + "/" + lineMagnitude + ")";
        String worldDistance = asinTheta + " * 6371.0) <= " + distance;

        FilterManager filterManager = FilterManager.getInstance();
        Location previousMin = filterManager.getViewPortMin();
        Location previousMax = filterManager.getViewPortMax();

        filterManager.setViewPortMin(minLat - kilometreInDegrees, minLon - kilometreInDegrees);
        filterManager.setViewPortMax(maxLat + kilometreInDegrees, maxLon + kilometreInDegrees);

        String filterWhere = filterManager.toString();

        filterManager.setViewPortMin(previousMin.getLatitude(), previousMin.getLongitude());
        filterManager.setViewPortMax(previousMax.getLatitude(), previousMax.getLongitude());

        String select = "severity";
        String from = "crashes, locations";
        String where = filterWhere + " AND " + worldDistance;

        List severityList = SqliteQueryBuilder.create()
                .with(tableName, constantsTable)
                .select(select)
                .from(from)
                .where(where)
                .buildGetter();
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
     */
    @FXML
    private void generateRouteAction() {
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
     * Overloaded function for handling route generation with favourites.
     *
     * @param favourite Favourite object with locations of route and filters.
     */
    private void generateRouteAction(Favourite favourite) {
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

    /**
     * Updates the ratingText label's text to the rating provided.
     *
     * @param rating string of numeric rating.
     */
    public void updateRatingLabel(String rating) {
        ratingText.setText("Rating: " + rating);
    }

    /**
     * Takes the list of coordinates stored in JavaScriptBridge and updates the rating shown
     * on the GUI's ratingText label through getting the overlapping points of each segment.
     *
     * @throws SQLException if there is an error
     *
     */
    public static void ratingUpdate() throws SQLException {
        List<Location> coordinates = JavaScriptBridge.getRouteMap()
                .get(JavaScriptBridge.getIndex());
        if (coordinates != null && !coordinates.isEmpty()) {
            double rating = getOverlappingPoints(coordinates);
            RoutingMenuController.controller.updateRatingLabel(Double.toString(Math.round(rating)));
        } else {
            System.out.println("No coordinates available for routeId: 0");
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
     * Enacts the selection of a given button when a click event occurs.
     * If the button is not already selected, it selects it.
     * Otherwise, it does nothing.
     *
     * @param event An ActionEvent called when the button is pressed.
     */
    public void toggleModeButton(ActionEvent event) {
        Button chosenButton = (Button) event.getSource();
        if (!Objects.equals(chosenButton, selectedButton)) {
            selectButton(chosenButton);
        }
    }

    /**
     * Takes a button to be selected.
     * If a different button is already selected, deselects this button and selects the new one.
     * Otherwise, just selects the new one.
     *
     * @param chosenButton Button to be selected.
     */
    public void selectButton(Button chosenButton) {
        if (!Objects.equals(chosenButton, selectedButton) && selectedButton != null) {
            selectedButton.getStyleClass().remove("clickedButtonColor");
            selectedButton.getStyleClass().add("hamburgerStyle");
        }
        selectedButton = chosenButton;
        chosenButton.getStyleClass().remove("hamburgerStyle");
        chosenButton.getStyleClass().add("clickedButtonColor");
        modeChoice = (String) chosenButton.getUserData();
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
        String mode = route.getTransportMode();

        // update textFields according to data
        startLocation.getEditor().setText(startLoc);
        endLocation.getEditor().setText(endLoc);
        stopLocation.getEditor().setText(stopLoc);
        for (Button button : transportButtons) {
            if (button.getUserData().equals(mode)) {
                selectButton(button);
            }
        }
    }

    /**
     * Updates the RouteManager's stored data with data currently in the routing menu.
     */
    @Override
    public void updateManager() {
        RouteManager route = RouteManager.getInstance();
        route.setStartLocation(startLocation.getEditor().getText());
        route.setEndLocation(endLocation.getEditor().getText());
        route.setStopLocation(stopLocation.getEditor().getText());
        route.setTransportMode(modeChoice);
    }

}
