package seng202.team0.gui;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.animation.Animation;
import javafx.event.ActionEvent;
import seng202.team0.business.FilterManager;
import seng202.team0.models.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Filter;

/**
 * Controller for the main.fxml window
 * @author seng202 teaching team & Willy T
 */


public class MainController {

    private static final Logger log = LogManager.getLogger(MainController.class);
    public WebView webView;

    @FXML
    private StackPane mainWindow;
    @FXML
    private Label defaultLabel;

    @FXML
    private Button defaultButton;
    @FXML
    private Button hamburgerButton;
    @FXML
    private AnchorPane transportModePane;
    @FXML
    private AnchorPane weatherPane;
    @FXML
    private AnchorPane regionsPane;
    @FXML
    private AnchorPane holidayPane;




    @FXML
    private Button helpButton;
    //weather pane
    @FXML
    private CheckBox selectAllWeather;


    @FXML
    private CheckBox selectAllTransport;

    // Severity Pane
    @FXML
    private AnchorPane severityPane;
    @FXML
    private CheckBox selectAllSeverity;
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

    //regions pane
    @FXML
    private CheckBox selectAllRegions;



    @FXML
    private AnchorPane includedMap;
    private Stage stage;

    private GeoLocator geolocator;
    private WebEngine webEngine;

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

        setCheckboxesUserData();

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
     * Toggles the visibility of various panes with fade animations.
     */
    public void toggleHamburger() {
        if (fadeTransition.getStatus() == Animation.Status.RUNNING) {
            fadeTransition.stop(); // Stop the animation if it's currently running
        }

        toggleHelpButtonVisibility(helpButton, 5);

        togglePaneWithFade(transportModePane, 0); // Pass an index to identify the pane
        togglePaneWithFade(weatherPane, 1);
        togglePaneWithFade(datePane, 2);
        togglePaneWithFade(regionsPane, 3);
        togglePaneWithFade(severityPane, 4);
        togglePaneWithFade(holidayPane, 6);

        // Toggle the visibility of the helpButton

        // Play each fade animation individually
        for (int i = 0; i < 7; i++) {
            fadeTransitions[i].play();
        }
    }

    /**
     * Toggles the visibility of the helpButton with a fade animation.
     * If the helpButton is currently visible, it will be faded out and hidden.
     * If the helpButton is currently hidden, it will be faded in and shown.
     */
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

    /**
     * Toggles the visibility of a given pane with a fade animation.
     *
     * @param pane The pane to toggle.
     * @param index The index of the fade transition in the array.
     */
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
    /**
     * Sets up a fade animation for an emoji button.
     *
     * @param button The emoji button to set up the animation for.
     * @param index The index of the emoji button in the array.
     */
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

    /**
     * Adds a location with given crash
     */
    private void addLocation(Crash crash) {
        javaScriptConnector.call("addMarker", crash.getCrashLocation1() + "-" + crash.getCrashLocation2(),
                crash.getLatitude(), crash.getLongitude());
    }

    private void displayRoute(Route... routes) {
        List<Route> routesList = new ArrayList<>();
        Collections.addAll(routesList, routes);
        javaScriptConnector.call("displayRoute", Route.routesToJSONArray(routesList));
    }

    /**
     * Removes the route from the WebView map (if currently shown)
     */
    private void removeRoute() {
        javaScriptConnector.call("removeRoute");
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
    private void generateStop() {
        Location stop = getStop();
        Location start = getStart();
        Location end = getEnd();
        if (start != null && end != null && stop != null) {
            Route route1 = new Route(start, stop);
            Route route2 = new Route(stop, end);

            List<Route> routesList = new ArrayList<>();
            routesList.add(route1);
            routesList.add(route2);
            javaScriptConnector.call("displayRoute", Route.routesToJSONArray(routesList));
        }
    }

    @FXML
    private void generateRouteAction() {
        Location start = getStart();
        Location end = getEnd();

        if (start != null && end != null) {
            Route route = new Route(start, end);
            displayRoute(route);
        }
    }

    @FXML
    public void sliderValueChange() {
        int sliderValue = (int)Math.round(dateSlider.getValue());

        // Update year label for user
        currentYearLabel.setText(Integer.toString(sliderValue));

        //
        FilterManager filters = FilterManager.getInstance();
        filters.setEarliestYear(sliderValue);
    }

    public void toggleAllCheckbox(CheckBox checkBox, AnchorPane parent) {
        FilterManager filters = FilterManager.getInstance();
        Object toAdd = checkBox.getUserData();

        System.out.println(toAdd.getClass());
        System.out.println(toAdd);

        // TODO check if logic event needs to check if the list contains the thing you are adding

        if (parent.equals(transportModePane)) {
            if (checkBox.isSelected()) {
                if (!filters.getModesSelected().contains((String) toAdd)) {
                    filters.addToModes((String) toAdd);
                }
            } else {
                filters.removeFromModes((String) toAdd);
            }
        } else if (parent.equals(weatherPane)) {
            if (checkBox.isSelected()) {
                if (!filters.getWeathersSelected().contains((String) toAdd)) {
                    filters.addToWeathers((String) toAdd);
                }
            } else {
                filters.removeFromWeathers((String) toAdd);
            }
        } else if (parent.equals(severityPane)) {
            if (checkBox.isSelected()) {
                if (!filters.getSeveritiesSelected().contains((Integer) toAdd)) {
                    filters.addToSeverities((Integer) toAdd);
                }
            } else {
                filters.removeFromSeverities((Integer) toAdd);
            }
        } else if (parent.equals(regionsPane)) {
            if (checkBox.isSelected()) {
                if (!filters.getRegionsSelected().contains((String) toAdd)) {
                    filters.addToRegions((String) toAdd);
                }
            } else {
                filters.removeFromRegions((String) toAdd);
            }
        } else if (parent.equals(holidayPane)) {
            System.out.println("CREATE HOLIDAY FILTERING");
        }
    }

    @FXML
    public void setCheckboxesUserData() {
        bicycleCheckBox.setUserData("bicycle_involved");
        busCheckBox.setUserData("bus_involved");
        carCheckBox.setUserData("car_involved");
        mopedCheckBox.setUserData("moped_involved");
        motorcycleCheckBox.setUserData("motorcycle_involved");
        parkedVehicleCheckBox.setUserData("parked_vehicle_involved");
        pedestrianCheckBox.setUserData("pedestrian_involved");
        schoolBusCheckBox.setUserData("school_bus_involved");
        trainCheckBox.setUserData("train_involved");
        truckCheckBox.setUserData("truck_involved");

        nonInjuryCheckBox.setUserData(1);
        minorCrashCheckBox.setUserData(2);
        majorCrashCheckBox.setUserData(4);
        deathCheckBox.setUserData(8);

        for (Object child : weatherVBox.getChildren()) {
            if (child instanceof CheckBox) {
                ((CheckBox) child).setUserData(((CheckBox) child).getText());
            }
        }

        for (Object child : leftRegionVBox.getChildren()) {
            if (child instanceof CheckBox) {
                ((CheckBox) child).setUserData(((CheckBox) child).getText());
            }
        }

        for (Object child : rightRegionVBox.getChildren()) {
            if (child instanceof CheckBox) {
                ((CheckBox) child).setUserData(((CheckBox) child).getText());
            }
        }
    }

    @FXML
    public void handleAllCheckBoxEvent(ActionEvent event) {
        CheckBox allCheckBox = (CheckBox) event.getSource();
        AnchorPane parent = (AnchorPane) allCheckBox.getParent().getParent();

        boolean allSelected = allCheckBox.isSelected();

        for (Object child : parent.getChildren()) {
            if (child instanceof VBox) {
                for (Object childCheckBox : ((VBox) child).getChildren()) {
                    if (childCheckBox instanceof  CheckBox) {
                        ((CheckBox) childCheckBox).setSelected(allSelected);
                        toggleAllCheckbox((CheckBox) childCheckBox, parent);
                    }
                }
            }
        }
    }

    @FXML
    public void handleCheckBoxEvent(ActionEvent event) {
        CheckBox checkBox = (CheckBox) event.getSource();
        AnchorPane parent = (AnchorPane) checkBox.getParent().getParent();

        toggleAllCheckbox(checkBox, parent);

        CheckBox allCheckBox = null;
        List<CheckBox> checkBoxes = new ArrayList<>();

        for (Object child : parent.getChildren()) {
            if (child instanceof VBox) {
                for (Object childCheckBox : ((VBox) child).getChildren()) {
                    if (childCheckBox instanceof  CheckBox) {
                        if (!Objects.equals(((CheckBox) childCheckBox).getText(), "All")) {
                            checkBoxes.add((CheckBox) childCheckBox);
                        } else {
                            allCheckBox = (CheckBox) childCheckBox;
                        }
                    }
                }
            }
        }

        assert allCheckBox != null;
        updateAllCheckBox(allCheckBox, checkBoxes);
    }

    public void updateAllCheckBox(CheckBox allCheckBox, List<CheckBox> checkBoxes) {
        boolean allSelected = true;
        for (CheckBox checkBox : checkBoxes) {
            if (!checkBox.isSelected()) {
                allSelected = false;
                break;
            }
        }
        allCheckBox.setSelected(allSelected);
    }

}
