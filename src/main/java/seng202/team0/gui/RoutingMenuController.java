package seng202.team0.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import seng202.team0.business.CrashManager;
import seng202.team0.business.FilterManager;
import seng202.team0.business.RouteManager;
import seng202.team0.models.*;
import seng202.team0.repository.FavouriteDAO;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

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

    private GeoLocator geolocator;
    private List<Location> stops = new ArrayList<>();
    private Button selectedButton = null;
    private String modeChoice;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        geolocator = new GeoLocator();

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

    private void displayRoute(Route... routes) { // String transportMode,
        List<Route> routesList = new ArrayList<>();
        Collections.addAll(routesList, routes);
        MainController.javaScriptConnector.call("displayRoute", Route.routesToJSONArray(routesList), "walking"); // transportMode
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
        Favourite favourite = new Favourite(startAddress, endAddress, start.latitude, start.longitude, end.latitude, end.longitude, filters);
        FavouriteDAO favorites = new FavouriteDAO();
        favorites.addOne(favourite);
    }


    @FXML
    private void displayRoutes() {
        FavouriteDAO favourites = new FavouriteDAO();
        List<Favourite> favouritesList = favourites.getAll();
        ObservableList<String> items = FXCollections.observableArrayList(favouritesList.stream().map(favourite -> {return favourite.getStartAddress()+" to "+favourite.getEndAddress();}).toList());
        loadRoutesComboBox.setItems(items);
    }

    @FXML
    private void loadRoute() throws SQLException {
        int favouriteID = loadRoutesComboBox.getSelectionModel().getSelectedIndex()+1;
        if (favouriteID != 0 && favouriteID != -1) {
            FavouriteDAO favourites = new FavouriteDAO();
            Favourite favourite = favourites.getOne(favouriteID);

            // Update FilterManager class with the filters associated to the favourite route and Checkboxes to match
            FilterManager filters = FilterManager.getInstance();
            filters.updateFiltersWithQueryString(favourite.getFilters());

            // Generates a route and makes sure stops is cleared
            stops.clear();
            generateRouteAction(favourite); // , transportMode);

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
     * @param route        The route for which overlapping crash points are to be identified.
     * @param dangerRadius The radius within which crash points are considered overlapping with the route.
     * @return A list of CrashInfo objects representing crash points that overlap with the route.
     * @throws SQLException If an SQL-related error occurs during database query execution.
     */
    public List<CrashInfo> getOverlappingPoints(Route route, double dangerRadius) throws SQLException {
        List<CrashInfo> overlappingPoints = new ArrayList<>();
        CrashManager crashManager = new CrashManager();
        List<CrashInfo> crashPoints = crashManager.getCrashLocations().stream().map(crash -> {
            HashMap crash1 = (HashMap) crash;
            double latitude = (double) crash1.get("latitude");
            double longitude = (double) crash1.get("longitude");
            int severity = (int) crash1.get("severity");
            return new CrashInfo(latitude, longitude, severity);
        }).toList();
        for (int i = 0; i < route.route.size() - 1; i++) {
            Location start = route.route.get(i);
            Location end = route.route.get(i + 1);

            for (CrashInfo crashPoint : crashPoints) {
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
     * Calculates the Haversine distance between two geographic coordinates using the Haversine formula.
     * The Haversine formula is used to compute the distance between two points on the Earth's surface
     * given their latitude and longitude coordinates.
     *
     * @param loc1 The first location with latitude and longitude coordinates.
     * @param loc2 The second location with latitude and longitude coordinates.
     * @return The Haversine distance between the two locations in meters.
     */
    public double haversineDistance(Location loc1, CrashInfo loc2) {
        double R = 6371000; // Earth radius in meters
        double dLat = Math.toRadians(loc2.lat - loc1.latitude);
        double dLon = Math.toRadians(loc2.lng - loc1.longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(loc1.latitude)) * Math.cos(Math.toRadians(loc2.lat))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    @FXML
    private void generateRouteAction() throws SQLException {
        Location start = getStart();
        Location end = getEnd();
        // String transportMode = getMode();

        if (start != null && end != null) {
            List<Location> routeLocations = new ArrayList<>();
            routeLocations.add(start);
            routeLocations.addAll(stops);  // add all the stops
            routeLocations.add(end);

            Route route = new Route(List.of(routeLocations.toArray(new Location[0])));
            List<CrashInfo> crashInfos = getOverlappingPoints(route,1000);
            int total = ratingGenerator(crashInfos);
            ratingText.setText("Danger: "+ total + "/5");
            numCrashesLabel.setText("Number of crashes on route: " + crashInfos.size());
            displayRoute(route); //, transportMode); // TODO
        }
    }

    private void getMode() {
        // TODO get the transport mode from the gui (add to gui)
    }

    private int ratingGenerator(List<CrashInfo> crashInfos) {
        int total = 0;
        for (CrashInfo crash: crashInfos) {
            total += crash.severity;
        }
        if (crashInfos.size() == 0) {
            total = (total*10) - 10;
        } else {
            total = (total*10 / crashInfos.size()) - 10;
        }
        if (total > 5) {
            total = 5;
        } else if (total < 0) {
            total = 0;
        }
        return total;
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
            List<CrashInfo> crashInfos = getOverlappingPoints(route,1000);
            int total = ratingGenerator(crashInfos);
            displayRoute(route); //, transportMode); // TODO
            ratingText.setText("Danger: "+ total + "/5");
            numCrashesLabel.setText("Number of crashes on route: " + crashInfos.size());
        }
    }

    public void toggleModeButton(ActionEvent event) {
        Button chosenButton = (Button) event.getSource();
        String modeChoice = (String) chosenButton.getUserData();

        if (Objects.equals(chosenButton, selectedButton)) {
            selectedButton = null; // deselects button
            chosenButton.getStyleClass().remove("clickedButtonColor");
            chosenButton.getStyleClass().add("sideBarColor");
        } else {
            selectedButton = chosenButton; // selects a different button
            chosenButton.getStyleClass().remove("sideBarColor");
            chosenButton.getStyleClass().add("clickedButtonColor");
        }
    }

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

    @Override
    public void updateManager() {
        RouteManager route = RouteManager.getInstance();
        route.setStartLocation(startLocation.getText());
        route.setEndLocation(endLocation.getText());
        route.setStopLocation(stopLocation.getText());
    }

}
