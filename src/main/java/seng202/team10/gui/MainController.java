package seng202.team10.gui;

import java.io.IOException;
import java.util.Objects;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team10.business.JavaScriptBridge;
import seng202.team10.business.RatingAreaManager;



/**
 * Controller for the main.fxml window
 *
 * @author Team 10
 */
public class MainController implements JavaScriptBridge.JavaScriptListener {
    private static final Logger log = LogManager.getLogger(MainController.class);
    public StackPane loadingScreen;
    public Label loadingPercentageLabel;
    @FXML
    private WebView webView;
    @FXML
    private StackPane mainWindow;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ImageView mySpinner;
    private Stage stage;
    private WebEngine webEngine;
    public static JSObject javaScriptConnector;
    private MapController mapController;
    @FXML
    private Button refreshButton;
    @FXML
    private AnchorPane menuDisplayPane;
    private String menuPopulated = "empty";
    private MenuController controller;
    private JavaScriptBridge javaScriptBridge;
    private Timeline progressBarTimeline;
    private Button selectedButton = null;
    @FXML
    private ProgressIndicator loadingSpinner;
    private RoutingMenuController routingMenuController;


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
        stage.setMinWidth(1280);
        stage.setMinHeight(800);
        stage.sizeToScene();
        loadingScreen.setVisible(true);
        webEngine = webView.getEngine();
        mapController = new MapController();
        mapController.setWebView(webView);
        mapController.init(stage);
        javaScriptBridge = mapController.getJavaScriptBridge();
        javaScriptBridge.setListener(this);
        javaScriptBridge.setMainController(this);
        loadMenuDisplayFromFxml("/fxml/empty_menu.fxml");
        initProgressBarTimeline();
    }


    /**
     * Initializes and manages the progress bar and its animation timeline.
     * This is for the loading screen.
     */
    private void initProgressBarTimeline() {
        progressBarTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(1),
                        new KeyValue(progressBar.progressProperty(), 1.0)
                )
        );
        progressBarTimeline.setCycleCount(1);

        progressBar.progressProperty().addListener((obs, oldVal, newVal) -> {
            int progressPercentage = (int) (newVal.doubleValue() * 100);
            loadingPercentageLabel.setText(progressPercentage + "%");
        }); //changing the percentage label as it loads.

        progressBarTimeline.play();

        webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
                javaScriptBridge.setCrashes();
                progressBarTimeline.stop();
                animateProgressBarToFull(progressBar);
            }
        }); //finishes the progress bar animation when the map is loaded.
    }


    /**
     * Animates a JavaFX ProgressBar to reach full (100%) progress.
     *
     * @param progressBar The ProgressBar to be animated.
     */
    private void animateProgressBarToFull(ProgressBar progressBar) {
        final Duration duration = Duration.millis(500);
        final Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(progressBar.progressProperty(), 1.0);

        KeyFrame keyFrame = new KeyFrame(duration, keyValue);

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    /**
     * Loads menu display in FXML file into menuDisplayPane.
     */
    private void loadMenuDisplayFromFxml(String filePath) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(filePath));
        try {
            StackPane menuDisplay = loader.load();
            menuDisplayPane.getChildren().setAll(menuDisplay);
            if (!menuPopulated.equals("empty") && !menuPopulated.equals("import")
                    && !menuPopulated.equals("help")) {
                controller = loader.getController();
            } //because nothing to update in these 3 menus.

        } catch (IOException ioException) {
            log.error(ioException);
        }
    }

    /**
     * Toggles the menu display dependent on the button clicked.
     */
    public void toggleMenuDisplay(ActionEvent event) {
        Button menuButton = (Button) event.getSource();

        toggleMenuButton(menuButton);

        String menuChoice = (String) menuButton.getUserData();

        if (!menuPopulated.equals("empty") && !menuPopulated.equals("import")
                && !menuPopulated.equals(("help"))) {
            controller.updateManager();
        } //because they don't have information to update

        if (menuPopulated.equals("rateArea")) {
            MainController.javaScriptConnector.call("drawingModeOff");
            RatingAreaManager.getInstance().clearBoundingBoxes();
        }

        if (Objects.equals(menuPopulated, menuChoice)) {
            menuPopulated = "empty";
            loadMenuDisplayFromFxml("/fxml/empty_menu.fxml");

        } else if (Objects.equals("routing", menuChoice)) {
            menuPopulated = menuChoice;
            loadMenuDisplayFromFxml("/fxml/routing_menu.fxml");

        } else if (Objects.equals("filtering", menuChoice)) {
            menuPopulated = menuChoice;
            loadMenuDisplayFromFxml("/fxml/filtering_menu.fxml");

        } else if (Objects.equals("settings", menuChoice)) {
            menuPopulated = menuChoice;
            loadMenuDisplayFromFxml("/fxml/settings_menu.fxml");

        } else if (Objects.equals("import", menuChoice)) {
            menuPopulated = menuChoice;
            loadMenuDisplayFromFxml("/fxml/import_window.fxml");

        } else if (Objects.equals("rateArea", menuChoice)) {
            menuPopulated = menuChoice;
            MainController.javaScriptConnector.call("drawingModeOn");
            loadMenuDisplayFromFxml("/fxml/rating_area_menu.fxml");

        } else if (Objects.equals("help", menuChoice)) {
            menuPopulated = menuChoice;
            loadMenuDisplayFromFxml("/fxml/help_menu.fxml");

        } else if (Objects.equals("graphing", menuChoice)) {
            menuPopulated = menuChoice;
            loadMenuDisplayFromFxml("/fxml/graph_window.fxml");
        }
    }


    /**
     * Changes the colour of the chosen button when clicked.
     *
     * @param chosenButton the button that was selected.
     */
    public void toggleMenuButton(Button chosenButton) {
        if (Objects.equals(chosenButton, selectedButton)) { // deselects
            selectedButton = null;
            chosenButton.getStyleClass().remove("clickedButtonColor");
            chosenButton.getStyleClass().add("menuButtonColor");
        } else if (!Objects.equals(chosenButton, selectedButton)
                && selectedButton != null) { // deselects and selects new
            selectedButton.getStyleClass().remove("clickedButtonColor");
            selectedButton.getStyleClass().add("menuButtonColor");
            selectedButton = chosenButton;
            chosenButton.getStyleClass().remove("menuButtonColor");
            chosenButton.getStyleClass().add("clickedButtonColor");
        } else { // selects new
            selectedButton = chosenButton;
            chosenButton.getStyleClass().remove("menuButtonColor");
            chosenButton.getStyleClass().add("clickedButtonColor");

        }
    }

    /**
     * Fades out a loading screen using a FadeTransition animation.
     */
    private void fadeOutLoadingScreen() {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(loadingScreen);
        fadeTransition.setDuration(Duration.millis(1000));
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setDelay(Duration.millis(1500));
        fadeTransition.setOnFinished(event -> {
            loadingScreen.setVisible(false);
        });
        // Start the fade out
        fadeTransition.play();
    }

    /**
     * Enables the refresh button.
     */
    public void enableRefresh() {
        refreshButton.setDisable(false);
    }

    /**
     * Disables the refresh button.
     */
    public void disableRefresh() {
        refreshButton.setDisable(false);
    }

    /**
     * Refreshes the data with a loading spinner and calls
     * to Javascript to update the data shown on Leaflet map.
     */
    public void refreshData() {
        showSpinner();
        new Thread(() -> {
            // Handle non-UI tasks here if any

            Platform.runLater(() -> {
                MainController.javaScriptConnector.call("updateDataShown");
                GraphController graphController = GraphController.graphController;
                if (graphController != null) {
                    graphController.updateGraph();
                }
                disableRefresh();
                hideSpinner();
            });
        }).start();
    }

    /**
     * Quits app with closing the window.
     */
    public void quitApp() {
        Platform.exit();
    }

    /**
     * This method is called when the map has finished loading.
     * It initiates the fading out of the loading screen.
     */
    @Override
    public void mapLoaded() {
        fadeOutLoadingScreen();
    }

    /**
     * This method shows the loading icon.
     * when called.
     */
    public void showSpinner() {
        Platform.runLater(() -> mySpinner.setVisible(true));
    }

    /**
     * When the task has finished loading.
     * this method hides the loading icon.
     */
    public void hideSpinner() {
        Platform.runLater(() -> mySpinner.setVisible(false));
    }


}