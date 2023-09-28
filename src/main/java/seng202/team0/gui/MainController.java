package seng202.team0.gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.business.CrashManager;
import seng202.team0.business.FilterManager;
import seng202.team0.models.*;
import seng202.team0.repository.FavouriteDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Controller for the main.fxml window
 * @author Team10
 */


public class MainController {

    private static final Logger log = LogManager.getLogger(MainController.class);
    public WebView webView;

    @FXML
    private StackPane mainWindow;

    @FXML
    private ComboBox<String> loadRoutesComboBox;

    @FXML
    private Label numCrashesLabel;

    @FXML
    public Label ratingText;


    private Stage stage;

    private GeoLocator geolocator;
    private WebEngine webEngine;

    private List<Location> stops = new ArrayList<>();


    JSObject javaScriptConnector;


    @FXML
    private TextField startLocation;

    @FXML
    private TextField endLocation;

    @FXML
    private TextField stopLocation;

    private MapController mapController;



    @FXML
    private AnchorPane settingsPane;


    @FXML
    private AnchorPane menuDisplayPane;

    private String menuPopulated = "empty";


    /**
     * Initializes the main stage, UI components, and the map controller.
     * This method sets up the primary stage, initializes a GeoLocator, maximizes
     * the stage, configures the map controller with the WebView, and initializes
     * the map. It also sets up a listener for WebView state changes to interact
     * with JavaScript code once it's loaded.
     *
     * @param stage The primary stage of the JavaFX application.
     * @throws NullPointerException If the 'stage' object is not properly initialized
     *                              before calling this method.
     */
    void init(Stage stage) {
        this.stage = stage;
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        geolocator = new GeoLocator();
        stage.setMaximized(true);
        mapController = new MapController();
        mapController.setWebView(webView);
        mapController.init(stage);
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

        stage.sizeToScene();
        webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    // if javascript loads successfully
                    if (newState == Worker.State.SUCCEEDED) {
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
                    }
                });

        loadEmptyMenuDisplay();
    }


    /**
     * Loads and displays the help window within the main application window.
     * This method uses JavaFX's FXMLLoader to load the content of the help window from an FXML file.
     * It clears the existing content in the main window and adds the help window's content.
     * The help window is anchored to the right side of the main window.
     */
    @FXML
    public void loadHelp() {
        try {
            FXMLLoader helpLoader = new FXMLLoader(getClass().getResource("/fxml/help_window.fxml"));
            Parent helpViewParent = helpLoader.load();

            // TODO maybe take out of function and put into something you can call for all loaders
            mainWindow.getChildren().clear();

            mainWindow.getChildren().add(helpViewParent);
            AnchorPane.setRightAnchor(helpViewParent,0d);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads empty menu display from FXML file.
     */
    private void loadEmptyMenuDisplay() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/empty_menu.fxml"));
        try {
            StackPane emptyMenuDisplay = loader.load();
            menuDisplayPane.getChildren().setAll(emptyMenuDisplay);
        } catch (IOException ioException) {
            log.error(ioException);
        }
    }

    /**
     * Loads filtering menu display from FXML file.
     */
    private void loadFilteringMenuDisplay() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/filtering_menu.fxml"));
        try {
            StackPane filteringMenuDisplay = loader.load();
            menuDisplayPane.getChildren().setAll(filteringMenuDisplay);
            FilteringMenuController filteringMenuController = loader.getController();
            filteringMenuController.updateCheckboxesWithFilterManager();
        } catch (IOException ioException) {
            log.error(ioException);
        }
    }

    /**
     * Loads settings menu display from FXML file.
     */
    private void loadSettingsMenuDisplay() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings_menu.fxml"));
        try {
            StackPane settingsMenuDisplay = loader.load();
            menuDisplayPane.getChildren().setAll(settingsMenuDisplay);
            SettingsMenuController settingsMenuController = loader.getController();
            settingsMenuController.setViewOptions();
        } catch (IOException ioException) {
            log.error(ioException);
        }
    }

    /**
     * Toggles the filtering menu display.
     */
    public void toggleFiltering() {
        if (Objects.equals(menuPopulated, "filtering")) {
            loadEmptyMenuDisplay();
            menuPopulated = "empty";
        } else {
            loadFilteringMenuDisplay();
            menuPopulated = "filtering";
        }
    }

    /**
     * Toggles the settings menu display.
     */
    public void toggleSettings() {
        if (Objects.equals(menuPopulated, "settings")) {
            loadEmptyMenuDisplay();
            menuPopulated = "empty";
        } else {
            loadSettingsMenuDisplay();
            menuPopulated = "settings";
        }
    }


    private void displayRoute(Route... routes) {
        List<Route> routesList = new ArrayList<>();
        Collections.addAll(routesList, routes);
        javaScriptConnector.call("displayRoute", Route.routesToJSONArray(routesList));
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
            displayRoute(route);
        }
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
            displayRoute(route);
            ratingText.setText("Danger: "+ total + "/5");
            numCrashesLabel.setText("Number of crashes on route: " + crashInfos.size());
        }
    }
}