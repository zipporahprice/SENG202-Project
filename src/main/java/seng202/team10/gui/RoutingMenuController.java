package seng202.team10.gui;

import static seng202.team10.business.RouteManager.getOverlappingPoints;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.PopOver;
import seng202.team10.business.FilterManager;
import seng202.team10.business.JavaScriptBridge;
import seng202.team10.business.RouteManager;
import seng202.team10.business.SettingsManager;
import seng202.team10.models.Favourite;
import seng202.team10.models.GeoLocator;
import seng202.team10.models.Location;
import seng202.team10.models.Review;
import seng202.team10.models.Route;
import seng202.team10.repository.SqliteQueryBuilder;




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

    private static final Logger log = LogManager.getLogger(RoutingMenuController.class);

    @FXML
    private ComboBox<String> startLocationComboBox;
    @FXML
    private ComboBox<String> endLocationComboBox;
    @FXML
    private ComboBox<String> stopLocationComboBox;
    @FXML
    private Button carButton;
    @FXML
    private Button bikeButton;
    @FXML
    private Button walkingButton;
    @FXML
    Button generateRoute;
    @FXML
    private Button removeRoute;

    @FXML
    private Button saveRouteButton;
    @FXML
    ListView<String> stopsListView = new ListView<>();
    @FXML
    ListView<String> favouritesListView = new ListView<>();

    public static RoutingMenuController controller;
    private GeoLocator geolocator;
    private List<Location> stops = new ArrayList<>();
    private Button selectedButton = null;
    private String modeChoice;
    private String startAddress;
    private String endAddress;
    private String stopAddress;
    private final List<Button> transportButtons = new ArrayList<>();
    private ObservableList<String> stopStrings = FXCollections.observableArrayList();
    private ObservableList<String> favouriteStrings = FXCollections.observableArrayList();
    private Favourite loadedFavourite;

    private PopOverController popOver;


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
        popOver = new PopOverController();
        carButton.setUserData("car");
        bikeButton.setUserData("bike");
        walkingButton.setUserData("walking");
        transportButtons.add(carButton);
        transportButtons.add(bikeButton);
        transportButtons.add(walkingButton);
        removeRoute.setDisable(true);
        stopsListView.setItems(stopStrings);
        stopsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        favouritesListView.setItems(favouriteStrings);
        favouritesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        controller = this;
        loadManager();
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
            popOver.showNotificationOnButtonPress(generateRoute, "Please select a transport option");
        } else {
            MainController.javaScriptConnector.call("displayRoute", Route
                    .routesToJsonArray(routesList), modeChoice);
        }
    }




    /**
     * Retrieves the start location based on user input from a TextField.
     *
     * @return The start Location object, or null if the input is empty or invalid.
     */
    @FXML
    private Location getStart() {
        String address = startAddress;
        System.out.println(startAddress);
        if (address == null) {
            return null;
        }
        Pair<Location, String> startResult = geolocator.getLocation(address);

        Location startMarker = startResult.getKey();
        String errorMessageStart = startResult.getValue();
        if (errorMessageStart != null) {
            popOver.showPopOver(errorMessageStart, startLocationComboBox, 5);
            return null;
        }

        return startMarker;
    }

    @FXML
    private void setStart() {
        String selectedItem = startLocationComboBox.getSelectionModel().getSelectedItem();
        loadedFavourite = null;
        if (selectedItem != null) {
            startAddress = selectedItem;
        }
    }

    @FXML
    private void loadStartOptions() {
        String address = startLocationComboBox.getEditor().getText().trim();
        ObservableList<String> addressOptions = geolocator.getAddressOptions(address);
        startLocationComboBox.setItems(addressOptions);
        startLocationComboBox.getEditor().setText(address);
    }

    /**
     * Retrieves the end location based on user input from a TextField.
     *
     * @return The end Location object, or null if the input is empty or invalid.
     */
    @FXML
    private Location getEnd() {
        String address = endAddress;
        System.out.println(endAddress);
        if (address == null) {
            return null;
        }
        Pair<Location, String> endResult = geolocator.getLocation(address);

        Location endMarker = endResult.getKey();
        String errorEndMessage = endResult.getValue();
        if (errorEndMessage != null) {
            popOver.showPopOver(errorEndMessage, endLocationComboBox, 5);
            return null;
        }
        return endMarker;

    }

    @FXML
    private void setEnd() {
        String selectedItem = endLocationComboBox.getSelectionModel().getSelectedItem();
        loadedFavourite = null;
        if (selectedItem != null) {
            endAddress = selectedItem;
        }
    }

    @FXML
    private void loadEndOptions() {
        String address = endLocationComboBox.getEditor().getText().trim();
        ObservableList<String> addressOptions = geolocator.getAddressOptions(address);
        endLocationComboBox.setItems(addressOptions);
        endLocationComboBox.getEditor().setText(address);

    }

    @FXML
    private Location getStop() {
        String address = stopAddress;
        if (address == null) {
            return null;
        }
        Pair<Location, String> stopResult = geolocator.getLocation(address);

        Location stopMarker = stopResult.getKey();
        String errorStopMessage = stopResult.getValue();
        if (errorStopMessage != null) {
            popOver.showPopOver(errorStopMessage, stopLocationComboBox, 5);
            return null;
        }

        return stopMarker;
    }

    @FXML
    private void setStop() {
        String selectedItem = stopLocationComboBox.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            stopAddress = selectedItem;
        }
    }

    @FXML
    private void loadStopOptions() {
        String address = stopLocationComboBox.getEditor().getText().trim();
        ObservableList<String> addressOptions = geolocator.getAddressOptions(address);
        stopLocationComboBox.setItems(addressOptions);
        stopLocationComboBox.getEditor().setText(address);

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
        String routeName = showRouteNameInputDialog();

        // List of favourite names
        List<String> favouriteNames = RouteManager.getFavouriteNames().stream().map((favourite) -> {
            HashMap<String, Object> favouriteHashmap = (HashMap<String, Object>) favourite;
            return (String) favouriteHashmap.get("route_name");
        }).toList();

        // Checks null, empty, and it is unique
        if (routeName == null || routeName.trim().isEmpty() || favouriteNames.contains(routeName)) {
            // Show error dialog

            popOver.showNotificationOnButtonPress(saveRouteButton, "Not a valid route!");
            return;
        }

        Favourite favourite = new Favourite(startAddress, endAddress,
                start.getLatitude(), start.getLongitude(), end.getLatitude(),
                end.getLongitude(), filters, modeChoice, routeName);

        List<Favourite> favourites = new ArrayList<>();
        favourites.add(favourite);

        SqliteQueryBuilder.create().insert("favourites").buildSetter(favourites);

        favouriteStrings.add(favourite.getName());
        favouritesListView.setItems(favouriteStrings);
    }

    private String showRouteNameInputDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save Route");
        dialog.setHeaderText("Enter a name for the route:");
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }


    /**
     * Loads a selected route or favorite location from the ComboBox.
     *
     * @throws SQLException If a database error occurs during the loading operation.
     */
    @FXML
    private void loadRoute() throws SQLException {
        String routeName = favouritesListView.getSelectionModel().getSelectedItem();
        if (routeName != null) {
            List<?> favouriteList = SqliteQueryBuilder.create()
                    .select("*")
                    .from("favourites")
                    .where("route_name = \"" + routeName + "\"")
                    .buildGetter();
            System.out.println("hello");
            System.out.println(favouriteList.size());
            Favourite favourite = (Favourite) favouriteList.get(0);
            loadedFavourite = favourite;

            // Update FilterManager class with the filters associated with the favourite route
            FilterManager filters = FilterManager.getInstance();
            filters.updateFiltersWithQueryString(favourite.getFilters());

            // Generates a route and makes sure stops are cleared

            startLocationComboBox.getEditor().setText(favourite.getStartAddress());
            endLocationComboBox.getEditor().setText(favourite.getEndAddress());
            startAddress = favourite.getStartAddress();
            endAddress = favourite.getEndAddress();
            for (Button button : transportButtons) {
                if (button.getUserData().equals(favourite.getTransportMode())) {
                    selectButton(button);
                }
            }
            stops.clear();
            stopsListView.getItems().clear();
            generateRouteAction(favourite);
            favouritesListView.getSelectionModel().clearSelection();

        }
    }

    @FXML
    private void deleteRoute() {
        if (favouritesListView.getSelectionModel().getSelectedItem() != null) {
            int selectedStopIndex = favouritesListView.getSelectionModel().getSelectedIndex();
            String name = favouritesListView.getSelectionModel().getSelectedItem();
            SqliteQueryBuilder.create().delete("favourites")
                    .where("route_name = \"" + name + "\"").buildDeleter();
            favouriteStrings.remove(selectedStopIndex);
        } else {
            favouriteStrings.remove(stopStrings.size() - 1);
        }

        favouritesListView.setItems(favouriteStrings);
    }


    /**
     * Adds a stop location to the collection and generates a route action.
     *
     */
    @FXML
    private void addStop() {
        Location stop = getStop();
        if (stop != null) {
            stops.add(stop);
            stopStrings.add(stopLocationComboBox.getValue());
            stopLocationComboBox.getEditor().setText(null);
        }
        generateRouteAction();
    }

    /**
     * Removes the last stop from the collection and generates a route.
     *
     */
    @FXML
    private void removeStop() {
        if (stops.size() >= 1) {
            if (stopsListView.getSelectionModel().getSelectedItem() != null) {
                int selectedStopIndex = stopsListView.getSelectionModel().getSelectedIndex();
                stops.remove(selectedStopIndex);
                stopStrings.remove(selectedStopIndex);
            } else {
                stops.remove(stopStrings.size() - 1);
                stopStrings.remove(stopStrings.size() - 1);
            }

            generateRouteAction();
        }
    }

    /**
     * Updates crash data and changes the current view if needed.
     *
     * @param crashes A list of crash data to update.
     */
    public static void updateCrashes(List<?> crashes) {
        JavaScriptBridge.updateCrashesByJavascript(crashes);
        if (SettingsManager.getInstance().getCurrentView().equals("None")) {
            SettingsManager.getInstance().setCurrentView("Crash Locations");
        }
        MainController.javaScriptConnector.call("updateView");
    }

    /**
     * Generates a route, calculates its danger rating, and updates the UI.
     *
     */
    @FXML
    private void generateRouteAction() {
        Location start = getStart();
        Location end = getEnd();
        if (loadedFavourite != null) {
            generateRouteAction(loadedFavourite);
        } else if (start != null && end != null) {
            routeLocations(start, end);
            removeRoute.setDisable(false);
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

        routeLocations(start, end);
    }

    private void routeLocations(Location start, Location end) {
        List<Location> routeLocations = new ArrayList<>();
        routeLocations.add(start);
        routeLocations.addAll(stops);
        routeLocations.add(end);

        Route route = new Route(List.of(routeLocations.toArray(new Location[0])));
        displayRoute(route);
    }

    /**
     * Takes the list of coordinates stored in JavaScriptBridge and updates the rating shown
     * on the GUI's ratingText label through getting the overlapping points of each segment.
     */
    public static void ratingUpdate() {
        try {
            List<Location> coordinates =
                    JavaScriptBridge.getRouteMap().get(JavaScriptBridge.getIndex());
            List<String> roads = JavaScriptBridge.getRoadsMap().get(JavaScriptBridge.getIndex());
            List<Double> distances =
                    JavaScriptBridge.getDistancesMap().get(JavaScriptBridge.getIndex());
            if (coordinates != null && !coordinates.isEmpty()) {
                Review review = getOverlappingPoints(coordinates, roads, distances);
                updateCrashes(review.crashes);
                MainController.javaScriptConnector.call("updateReviewContent", review.toString());

            } else {
                System.out.println("No coordinates available for routeId: 0");
            }
        } catch (SQLException e) {
            log.error(e);
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
        MainController.javaScriptConnector.call("resetLayers");
        removeRoute.setDisable(true);

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
        selectButton(chosenButton);
    }

    /**
     * Takes a button to be selected.
     * If a different button is already selected, deselects this button and selects the new one.
     * Otherwise, just selects the new one.
     *
     * @param chosenButton Button to be selected.
     */
    public void selectButton(Button chosenButton) {
        if (!Objects.equals(chosenButton, selectedButton)) {
            if (!Objects.equals(chosenButton, selectedButton) && selectedButton != null) {
                selectedButton.getStyleClass().remove("clickedButtonColor");
                selectedButton.getStyleClass().add("hamburgerStyle");
            }
            selectedButton = chosenButton;
            selectedButton.getStyleClass().remove("hamburgerStyle");
            selectedButton.getStyleClass().add("clickedButtonColor");
            modeChoice = (String) chosenButton.getUserData();
        }
    }

    /**
     * Loads the route data stored from the RouteManager into the routing menu.
     */
    @Override
    public void loadManager() {
        //List<String>
        favouriteStrings = FXCollections.observableArrayList(RouteManager
                .getFavouriteNames().stream().map((favourite) -> {
                    HashMap<String, Object> favouriteHashmap = (HashMap<String, Object>) favourite;
                    return (String) favouriteHashmap.get("route_name");
                }).toList());

        favouritesListView.getItems().addAll(favouriteStrings);

        RouteManager route = RouteManager.getInstance();

        // retrieve all updated location data
        String startLoc = route.getStartLocation();
        String endLoc = route.getEndLocation();
        String stopLoc = route.getStopLocation();
        String mode = route.getTransportMode();

        // update textFields according to data
        startLocationComboBox.getEditor().setText(startLoc);
        endLocationComboBox.getEditor().setText(endLoc);
        stopLocationComboBox.getEditor().setText(stopLoc);
        startAddress = startLoc;
        stopAddress = stopLoc;
        endAddress = endLoc;
        for (Button button : transportButtons) {
            if (button.getUserData().equals(mode)) {
                selectButton(button);
            }
        }

        // update clear button based on manager
        removeRoute.setDisable(route.getRemoveRouteDisabled());
    }

    /**
     * Updates the RouteManager's stored data with data currently in the routing menu.
     */
    @Override
    public void updateManager() {
        RouteManager route = RouteManager.getInstance();
        route.setStartLocation(startLocationComboBox.getEditor().getText());
        route.setEndLocation(endLocationComboBox.getEditor().getText());
        route.setStopLocation(stopLocationComboBox.getEditor().getText());
        route.setTransportMode(modeChoice);
        route.setRemoveRouteDisabled(removeRoute.isDisabled());
    }

}
