package seng202.team0.gui;

import javafx.concurrent.Worker;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
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
    private AnchorPane boundariesPane;

    @FXML
    private Button helpButton;

    @FXML
    private CheckBox selectAllWeather;
    @FXML
    private CheckBox snow;
    @FXML
    private CheckBox fine;
    @FXML
    private CheckBox heavyRain;
    @FXML
    private CheckBox lightRain;
    @FXML
    private CheckBox mistOrFog;

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
    @FXML
    private AnchorPane holidayPane;

    // Date Pane
    @FXML
    private AnchorPane datePane;
    @FXML
    private Slider dateSlider;
    @FXML
    private Label currentYearLabel;


    @FXML
    private AnchorPane includedMap;
    private Stage stage;

    private GeoLocator geolocator;
    private WebEngine webEngine;

    JSObject javaScriptConnector;

    private FadeTransition fadeTransition = new FadeTransition(Duration.millis(500));
    private FadeTransition[] emojiButtonTransitions = new FadeTransition[6];
    private boolean[] emojiButtonClicked = new boolean[6];  // Keep track of button states
    private FadeTransition[] fadeTransitions = new FadeTransition[6]; // Array to store individual fade transitions
    private ArrayList<CheckBox> weatherCheckboxes = new ArrayList<CheckBox>();
    private ArrayList<CheckBox> transportCheckboxes = new ArrayList<CheckBox>();
    private ArrayList<CheckBox> severityCheckboxes = new ArrayList<CheckBox>();


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

        selectAllWeather.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                System.out.println("Select All weather includes crashes with no weather data");

                for (CheckBox weather : weatherCheckboxes) {
                    weather.setSelected(newValue);
                }

            }
        });

        selectAllTransport.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                for (CheckBox transportMode : transportCheckboxes) {
                    transportMode.setSelected(newValue);
                }
            }
        });

        selectAllSeverity.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                for (CheckBox severity : severityCheckboxes) {
                    severity.setSelected(newValue);
                }
            }
        });

        //TODO make the size of the filters resize with the window size, below code unfinished


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
        boundariesPane.setVisible(false);
        severityPane.setVisible(false);

        weatherCheckboxes = new ArrayList<CheckBox>() {
            {
                add(snow);
                add(fine);
                add(heavyRain);
                add(lightRain);
                add(mistOrFog);
            }
        };

        for (CheckBox weather : weatherCheckboxes) {
            weather.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (weather.isSelected() == false) {
                        selectAllWeather.setSelected(false);
                    }
                }
            });
        }

        transportCheckboxes = new ArrayList<CheckBox>() {
            {
                add(bicycleCheckBox);
                add(busCheckBox);
                add(carCheckBox);
                add(mopedCheckBox);
                add(motorcycleCheckBox);
                add(parkedVehicleCheckBox);
                add(pedestrianCheckBox);
                add(schoolBusCheckBox);
                add(trainCheckBox);
                add(truckCheckBox);
            }
        };
        for (CheckBox transportMode : transportCheckboxes) {
            transportMode.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (transportMode.isSelected() == false) {
                        selectAllTransport.setSelected(false);
                    }
                }
            });
        }

        severityCheckboxes = new ArrayList<CheckBox>() {
            {
                add(nonInjuryCheckBox);
                add(minorCrashCheckBox);
                add(majorCrashCheckBox);
                add(deathCheckBox);
            }
        };

        for (CheckBox severity : severityCheckboxes) {
            severity.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (severity.isSelected() == false) {
                        selectAllSeverity.setSelected(false);
                    }
                }
            });
        }
        //TODO modularise the unticking of checkboxes

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
        togglePaneWithFade(boundariesPane, 3);
        togglePaneWithFade(severityPane, 4);
        togglePaneWithFade(holidayPane, 5);

        // Toggle the visibility of the helpButton

        // Play each fade animation individually
        for (int i = 0; i < 6; i++) {
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
    public void handleSeverityCheckBoxEvent(ActionEvent event) {
        CheckBox checkBox = (CheckBox)event.getSource();
        int severity = 0;

        if (checkBox.equals(nonInjuryCheckBox)) {
            severity = 1;
        } else if (checkBox.equals(minorCrashCheckBox)) {
            severity = 2;
        } else if (checkBox.equals(majorCrashCheckBox)) {
            severity = 4;
        } else if (checkBox.equals(deathCheckBox)) {
            severity = 8;
        }

        FilterManager filters = FilterManager.getInstance();
        if (checkBox.isSelected()) {
            filters.addToSeverities(severity);
        } else {
            filters.removeFromSeverities(severity);
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

    @FXML
    public void handleTypeInvolved(ActionEvent event) {
        CheckBox checkBox = (CheckBox)event.getSource();
        String mode = null;

        if (checkBox.equals(bicycleCheckBox)) {
            mode = "bicycle_involved";
        } else if (checkBox.equals(busCheckBox)) {
            mode = "bus_involved";
        } else if (checkBox.equals(carCheckBox)) {
            mode = "car_involved";
        } else if (checkBox.equals(mopedCheckBox)) {
            mode = "moped_involved";
        } else if (checkBox.equals(motorcycleCheckBox)) {
            mode = "motorcycle_involved";
        } else if (checkBox.equals(parkedVehicleCheckBox)) {
            mode = "parked_vehicle_involved";
        } else if (checkBox.equals(pedestrianCheckBox)) {
            mode = "pedestrian_involved";
        } else if (checkBox.equals(schoolBusCheckBox)) {
            mode = "school_bus_involved";
        } else if (checkBox.equals(trainCheckBox)) {
            mode = "train_involved";
        } else if (checkBox.equals(truckCheckBox)) {
            mode = "truck_involved";
        }
        FilterManager filters = FilterManager.getInstance();
        if (checkBox.isSelected()) {
            filters.addToModes(mode);
        } else {
            filters.removeFromModes(mode);
        }
    }
}
