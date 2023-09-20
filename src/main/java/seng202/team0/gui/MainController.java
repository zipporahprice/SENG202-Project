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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import netscape.javascript.JSObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.business.CrashManager;
import seng202.team0.business.FilterManager;
import seng202.team0.models.*;
import seng202.team0.repository.FavouriteDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
    private AnchorPane transportModePane;
    @FXML
    private AnchorPane weatherPane;
    @FXML
    private AnchorPane regionsPane;
    @FXML
    private ComboBox<String> loadRoutesComboBox;
    @FXML
    private AnchorPane holidayPane;

    @FXML
    private Label numCrashesLabel;


    @FXML
    private Button helpButton;

    // Severity Pane
    @FXML
    private AnchorPane severityPane;
    @FXML
    private CheckBox nonInjuryCheckBox;
    @FXML
    private CheckBox minorCrashCheckBox;
    @FXML
    private CheckBox majorCrashCheckBox;
    @FXML
    private CheckBox deathCheckBox;
    @FXML
    private CheckBox bicycleCheckBox;
    @FXML
    private CheckBox busCheckBox;
    @FXML
    private CheckBox carCheckBox;
    @FXML
    private CheckBox mopedCheckBox;
    @FXML
    private CheckBox motorcycleCheckBox;
    @FXML
    private CheckBox parkedVehicleCheckBox;
    @FXML
    private CheckBox pedestrianCheckBox;
    @FXML
    private CheckBox schoolBusCheckBox;
    @FXML
    private CheckBox trainCheckBox;
    @FXML
    private CheckBox truckCheckBox;

    // Date Pane
    @FXML
    private AnchorPane datePane;
    @FXML
    private Slider dateSlider;
    @FXML
    private Label currentYearLabel;

    @FXML
    private ChoiceBox viewChoiceBox;

    @FXML
    public Label ratingText;


    private Stage stage;

    private GeoLocator geolocator;
    private WebEngine webEngine;

    private List<Location> stops = new ArrayList<>();


    JSObject javaScriptConnector;

    private FadeTransition fadeTransition = new FadeTransition(Duration.millis(500));
    private FadeTransition[] emojiButtonTransitions = new FadeTransition[6];
    private boolean[] emojiButtonClicked = new boolean[6];  // Keep track of button states
    private FadeTransition[] fadeTransitions = new FadeTransition[7]; // Array to store individual fade transitions

    @FXML
    private VBox weatherVBox;
    @FXML
    private VBox leftRegionVBox;
    @FXML
    private VBox rightRegionVBox;


    @FXML
    private Button carButton;
    @FXML
    private Button bikeButton;
    @FXML
    private Button busButton;
    @FXML
    private Button walkingButton;
    @FXML
    private Button helicopterButton;
    @FXML
    private Button motorbikeButton;

    @FXML
    private TextField startLocation;

    @FXML
    private TextField endLocation;

    @FXML
    private TextField stopLocation;

    private MapController mapController;

    public static String currentView = "Automatic";

    @FXML
    private AnchorPane settingsPane;

    // Helper classes
    private CheckBoxHelper checkBoxHelper;




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
        geolocator = new GeoLocator();
        stage.setMaximized(true);
        mapController = new MapController();
        mapController.setWebView(webView);
        mapController.init(stage);
        loadRoutesComboBox.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
            if (isNowShowing) {
                // The ComboBox is now showing its list.
                ListView<?> lv = (ListView<?>) loadRoutesComboBox.lookup(".list-view");
                if (lv != null && lv instanceof ListView) {
                    lv.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
                        Object selectedItem = lv.getSelectionModel().getSelectedItem();
                        if (selectedItem != null) {
                            System.out.println("Clicked on: " + selectedItem);
                            try {
                                loadRoute();
                            } catch (SQLException e) {
                                log.error(new RuntimeException(e));
                            }
                        }
                    });
                }
            }
        });

        checkBoxHelper = new CheckBoxHelper(severityPane, transportModePane, dateSlider,
                currentYearLabel, weatherPane, regionsPane, holidayPane);

        setViewOptions();

        stage.sizeToScene();
        webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    // if javascript loads successfully
                    if (newState == Worker.State.SUCCEEDED) {
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
                    }
                });
    }



    @FXML
    private void initialize() {
        setupEmojiButtonTransition(carButton, 0);
        setupEmojiButtonTransition(bikeButton, 1);
        setupEmojiButtonTransition(busButton, 2);
        setupEmojiButtonTransition(walkingButton, 3);
        setupEmojiButtonTransition(helicopterButton, 4);
        setupEmojiButtonTransition(motorbikeButton, 5);
        helpButton.setVisible(false);
        transportModePane.setVisible(false);
        weatherPane.setVisible(false);
        datePane.setVisible(false);
        regionsPane.setVisible(false);
        severityPane.setVisible(false);
        holidayPane.setVisible(false);



    }
    /**
     * Loads and displays the help window within the main application window.
     * This method uses JavaFX's FXMLLoader to load the content of the help window from an FXML file.
     * It clears the existing content in the main window and adds the help window's content.
     * The help window is anchored to the right side of the main window.
     */
    public void loadHelp() {
        try {
            FXMLLoader helpLoader = new FXMLLoader(getClass().getResource("/fxml/help_window.fxml"));
            Parent helpViewParent = helpLoader.load();

            // TODO maybe take out of function and put into something you can call for all loaders
            mainWindow.getChildren().clear();

            mainWindow.getChildren().add(helpViewParent);
            AnchorPane.setRightAnchor(helpViewParent,0d);
        } catch (IOException e) {
            log.error(e);
        }
    }
    /**
     * Toggles the visibility of various panes with fade animations.
     */
    public void toggleHamburger() {
        if (fadeTransition.getStatus() == Animation.Status.RUNNING) {
            fadeTransition.stop(); // Stop the animation if it's currently running
        }

        togglePaneWithFade(transportModePane, 0); // Pass an index to identify the pane
        togglePaneWithFade(weatherPane, 1);
        togglePaneWithFade(datePane, 2);
        togglePaneWithFade(regionsPane, 3);
        togglePaneWithFade(severityPane, 4);
        toggleHelpButtonVisibility(helpButton, 5);
        togglePaneWithFade(holidayPane, 6);

        // Toggle the visibility of the helpButton

        // Play each fade animation individually
        for (int i = 0; i < 7; i++) {
            fadeTransitions[i].play();
        }
    }


    private void toggleHelpButtonVisibility(Button helpButton, int index) {
        if (fadeTransitions[index] == null) {
            fadeTransitions[index] = new FadeTransition(Duration.millis(500), helpButton);
            fadeTransitions[index].setFromValue(0.0); // Start from fully transparent (invisible)
            fadeTransitions[index].setToValue(1.0);   // Transition to fully visible
        }

        if (helpButton.isVisible()) {
            System.out.println("visible help button");
            fadeTransitions[index].setOnFinished(event -> helpButton.setVisible(false)); // Set the action to hide the helpButton after fade-out
            fadeTransitions[index].setFromValue(1.0); // Start from fully visible
            fadeTransitions[index].setToValue(0.0);   // Transition to fully transparent (invisible)
        } else {
            helpButton.setVisible(true); // Make the helpButton visible before starting fade-in
            fadeTransitions[index].setOnFinished(null); // Reset the onFinished handler
            fadeTransitions[index].setFromValue(0.0);   // Start from fully transparent (invisible)
            fadeTransitions[index].setToValue(1.0);     // Transition to fully visible
        }
    }


    private void togglePaneWithFade(AnchorPane pane, int index) {
        if (fadeTransitions[index] == null) {
            fadeTransitions[index] = new FadeTransition(Duration.millis(500), pane);
            fadeTransitions[index].setFromValue(0.0); // Start from fully transparent (invisible)
            fadeTransitions[index].setToValue(1.0);   // Transition to fully visible
        }

        if (pane.isVisible()) {
            fadeTransitions[index].setOnFinished(event -> pane.setVisible(false)); // Set the action to hide the pane after fade-out
            fadeTransitions[index].setFromValue(1.0); // Start from fully visible
            fadeTransitions[index].setToValue(0.0);   // Transition to fully transparent (invisible)
        } else {
            pane.setVisible(true); // Make the pane visible before starting fade-in
            fadeTransitions[index].setOnFinished(null); // Reset the onFinished handler
            fadeTransitions[index].setFromValue(0.0);   // Start from fully transparent (invisible)
            fadeTransitions[index].setToValue(1.0);     // Transition to fully visible
        }
    }

    private void setupEmojiButtonTransition(Button button, int index) {
        emojiButtonTransitions[index] = new FadeTransition(Duration.millis(300));
        emojiButtonTransitions[index].setNode(button);
        emojiButtonTransitions[index].setFromValue(1.0);  // Fully visible
        emojiButtonTransitions[index].setToValue(1.0);    // Semi-transparent
        emojiButtonTransitions[index].setOnFinished(event -> {
            if (emojiButtonClicked[index]) {
                button.getStyleClass().remove("clickedButtonColor");
            } else {
                button.getStyleClass().add("clickedButtonColor");
            }
            emojiButtonClicked[index] = !emojiButtonClicked[index];
        });
    }
    /**
     * Handles the click event of an emoji button.
     *
     * @param event The action event triggered by the emoji button click.
     */
    public void handleEmojiButtonClick(ActionEvent event) {
        Button button = (Button) event.getSource();
        int buttonIndex = Integer.parseInt(button.getUserData().toString());

        if (emojiButtonClicked[buttonIndex]) {
            emojiButtonTransitions[buttonIndex].setRate(-1);  // Reverse the animation when clicked again
        } else {
            emojiButtonTransitions[buttonIndex].setRate(1);
        }
        emojiButtonTransitions[buttonIndex].play();
    }


    private void displayRoute(Route... routes) {
        List<Route> routesList = new ArrayList<>();
        Collections.addAll(routesList, routes);
        javaScriptConnector.call("displayRoute", Route.routesToJSONArray(routesList));
    }



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

            // Updates FilterManager singleton with Favourite's filters and Checkboxes to match
            checkBoxHelper.updateCheckboxesWithFavourites(favourite);

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
            ratingText.setText("Danger: "+ total + "/5");
            numCrashesLabel.setText("Number of crashes on route: " + crashInfos.size());
            displayRoute(route);
        }
    }


    /**
     * Handles the change in the slider value and updates the user interface accordingly.
     * This method is called when the user interacts with a slider to select a value.
     * It rounds the slider value to an integer, updates the year label, and sets the earliest year filter.
     */
    @FXML
    public void sliderValueChange() {
        int sliderValue = (int)Math.round(dateSlider.getValue());

        // Update year label for user
        currentYearLabel.setText(Integer.toString(sliderValue));

        // Updates Filter Manager with the earliest year for crash query
        FilterManager filters = FilterManager.getInstance();
        filters.setEarliestYear(sliderValue);
    }

    /**
     * Handles the event triggered when an "All" CheckBox is selected or deselected.
     * This method is typically used to select or deselect all other related CheckBoxes within the same group.
     *
     * @param event The ActionEvent triggered by the "All" CheckBox.
     */
    @FXML
    public void handleAllCheckBoxEvent(ActionEvent event) {
        // Initialise parent to search through and what to set
        CheckBox allCheckBox = (CheckBox) event.getSource();
        AnchorPane parent = (AnchorPane) allCheckBox.getParent().getParent();
        boolean allSelected = allCheckBox.isSelected();

        // Use helper function to set all checkboxes to the same state as all checkbox
        checkBoxHelper.setCheckBoxesFromAllCheckBoxState(parent, allSelected);
    }

    /**
     * Handles the event triggered when a CheckBox is selected or deselected.
     * This method is responsible for updating filters and potentially the "All" CheckBox state
     * within the same group of CheckBoxes.
     *
     * @param event The ActionEvent triggered by the CheckBox.
     */
    @FXML
    public void handleCheckBoxEvent(ActionEvent event) {
        CheckBox checkBox = (CheckBox) event.getSource();
        AnchorPane parent = (AnchorPane) checkBox.getParent().getParent();

        checkBoxHelper.addToFilters(checkBox, parent);

        // Runs helper function to get all checkbox and list of other checkboxes
        Pair<CheckBox, List<CheckBox>> result = checkBoxHelper.getAllCheckBoxAndCheckBoxList(parent);
        CheckBox allCheckBox = result.getLeft();
        List<CheckBox> checkBoxes = result.getRight();

        assert allCheckBox != null;
        checkBoxHelper.updateAllCheckBox(allCheckBox, checkBoxes);
    }

    @FXML
    private void toggleAnchorPaneVisibility() {
        settingsPane.setVisible(!settingsPane.isVisible());
    }

    // TODO look at creating a new SettingsHelper
    private void setViewOptions() {
        viewChoiceBox.getItems().addAll("Automatic", "Heatmap", "Crash Locations");
        viewChoiceBox.setValue("Automatic");
        viewChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentView = (String) newValue;
            }
        });
    }
}