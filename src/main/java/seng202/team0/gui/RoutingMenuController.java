package seng202.team0.gui;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import seng202.team0.business.CrashManager;
import seng202.team0.business.FilterManager;
import seng202.team0.business.RouteManager;
import seng202.team0.models.Crash;
import seng202.team0.models.Favourite;
import seng202.team0.models.GeoLocator;
import seng202.team0.models.Location;
import seng202.team0.models.Route;
import seng202.team0.repository.FavouriteDAO;

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

    @FXML
    private Button removeRoute;

    private GeoLocator geolocator;
    private List<Location> stops = new ArrayList<>();
    private Button selectedButton = null;
    private String modeChoice;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        geolocator = new GeoLocator();
        carButton.setUserData("car");
        bikeButton.setUserData("bike");
        walkingButton.setUserData("walking");

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
        loadManager();
    }

    private void displayRoute(Route... routes) {
        List<Route> routesList = new ArrayList<>();
        Collections.addAll(routesList, routes);
        if(modeChoice== null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No Mode of Transport Selected!" +
                    "\nPlease Select a mode of Transport");

            alert.showAndWait();
            return;
        } else {
            MainController.javaScriptConnector.call("displayRoute", Route
                    .routesToJSONArray(routesList), modeChoice);
        }
    }
    


    /**
     * Adds a location when the "Add Location" button is clicked.
     * Uses Geolocator class to turn the address into a lat, lng pair
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



    @FXML
    private void saveRoute() throws SQLException {
        Location start = getStart();
        Location end = getEnd();
        String filters = FilterManager.getInstance().toString();
        String startAddress = geolocator.getAddress(start.latitude, start.longitude);
        String endAddress = geolocator.getAddress(end.latitude, end.longitude);
        Favourite favourite = new Favourite(startAddress, endAddress,
                start.latitude, start.longitude, end.latitude, end.longitude, filters);
        FavouriteDAO favorites = new FavouriteDAO();
        favorites.addOne(favourite);
    }


    @FXML
    private void displayRoutes() {
        FavouriteDAO favourites = new FavouriteDAO();
        List<Favourite> favouritesList = favourites.getAll();
        ObservableList<String> items = FXCollections.observableArrayList(favouritesList
                .stream().map(favourite -> {
                    return favourite.getStartAddress()
                        + " to " + favourite.getEndAddress(); }).toList());
        loadRoutesComboBox.setItems(items);
    }

    @FXML
    private void loadRoute() throws SQLException {
        int favouriteId = loadRoutesComboBox.getSelectionModel().getSelectedIndex() + 1;
        if (favouriteId != 0 && favouriteId != -1) {
            FavouriteDAO favourites = new FavouriteDAO();
            Favourite favourite = favourites.getOne(favouriteId);

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


    @FXML
    private void addStop() throws SQLException {
        Location stop = getStop();
        if (stop != null) {
            stops.add(stop);
        }
        generateRouteAction();
    }

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
    public List<Crash> getOverlappingPoints(Route route, double dangerRadius) throws SQLException {
        List<Crash> overlappingPoints = new ArrayList<>();
        CrashManager crashManager = new CrashManager();
        List<Crash> crashPoints = crashManager.getCrashLocations().stream().map(crash -> {
            HashMap crash1 = (HashMap) crash;
            double latitude = (double) crash1.get("latitude");
            double longitude = (double) crash1.get("longitude");
            int severity = (int) crash1.get("severity");
            return new Crash(latitude, longitude, severity);
        }).toList();
        for (int i = 0; i < route.route.size() - 1; i++) {
            Location start = route.route.get(i);
            Location end = route.route.get(i + 1);

            for (Crash crashPoint : crashPoints) {
                double distToStart = haversineDistance(start, crashPoint);
                double distToEnd = haversineDistance(end, crashPoint);

                if (distToStart < dangerRadius || distToEnd < dangerRadius) {
                    // Check if crash point is along the extended line of the segment
                    overlappingPoints.add(crashPoint);
                }
            }
        }

        return overlappingPoints;
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
        double deltaLat = Math.toRadians(loc2.getLatitude() - loc1.latitude);
        double deltaLon = Math.toRadians(loc2.getLongitude() - loc1.longitude);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(Math.toRadians(loc1.latitude))
                * Math.cos(Math.toRadians(loc2.getLatitude()))
                * Math.sin(deltaLon  / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return r * c;
    }

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
            displayRoute(route);
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

        if ((startLocation.getText() == null || startLocation.getText().isEmpty()) ||
                (endLocation.getText() == null || endLocation.getText().isEmpty())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No route to remove!");
            alert.showAndWait();

            return;
        }
        MainController.javaScriptConnector.call("removeRoute");

        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("clickedButtonColor");
            selectedButton.getStyleClass().add("hamburgerStyle");
            selectedButton = null; // Reset the reference after resetting its styles
        }

        startLocation.setText("");
        endLocation.setText("");

    }
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
            displayRoute(route);
            ratingText.setText("Danger: " + total + "/5");
            numCrashesLabel.setText("Number of crashes on route: " + crashInfos.size());
        }
    }


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
            selectedButton = null;
            chosenButton.getStyleClass().remove("clickedButtonColor");
            chosenButton.getStyleClass().add("hamburgerStyle");
        } else if (!Objects.equals(chosenButton, selectedButton) && selectedButton != null) {
            selectedButton.getStyleClass().remove("clickedButtonColor");
            selectedButton.getStyleClass().add("hamburgerStyle");
            selectedButton = chosenButton;
            chosenButton.getStyleClass().remove("hamburgerStyle");
            chosenButton.getStyleClass().add("clickedButtonColor");
        } else {
            selectedButton = chosenButton;
            chosenButton.getStyleClass().remove("hamburgerStyle");
            chosenButton.getStyleClass().add("clickedButtonColor");

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
