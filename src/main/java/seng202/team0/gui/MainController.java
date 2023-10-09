package seng202.team0.gui;

import java.io.IOException;
import java.util.Objects;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.application.Platform;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.models.JavaScriptBridge;


/**
 * Controller for the main.fxml window
 *
 * @author Team10
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
    private Stage stage;
    private WebEngine webEngine;
    public static JSObject javaScriptConnector;
    private MapController mapController;
    @FXML
    private AnchorPane menuDisplayPane;
    private String menuPopulated = "empty";
    private MenuController controller;

    private JavaScriptBridge javaScriptBridge;

    private Timeline progressBarTimeline;



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
        stage.setMinWidth(1000);
        stage.setMinHeight(800);
        stage.setMaximized(true);
        stage.sizeToScene();

        loadingScreen.setVisible(true);
        webEngine = webView.getEngine();
        mapController = new MapController();
        mapController.setWebView(webView);
        mapController.init(stage);
        javaScriptBridge = mapController.getJavaScriptBridge();
        javaScriptBridge.setListener(this);
        loadMenuDisplayFromFxml("/fxml/empty_menu.fxml");
        initProgressBarTimeline();


    }

    private void initProgressBarTimeline() {
        progressBarTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(1), // Adjust this based on expected average loading time
                        new KeyValue(progressBar.progressProperty(), 1.0)
                )
        );
        progressBarTimeline.setCycleCount(1);

        // Listen to the progress property of the ProgressBar
        progressBar.progressProperty().addListener((obs, oldVal, newVal) -> {
            int progressPercentage = (int) (newVal.doubleValue() * 100);
            loadingPercentageLabel.setText(progressPercentage + "%");
        });

        progressBarTimeline.play();

        webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");

                // Stop the ongoing Timeline and fill the ProgressBar to 100%
                progressBarTimeline.stop();
                animateProgressBarToFull(progressBar);
            }
        });
    }

    private void animateProgressBarToFull(ProgressBar progressBar) {
        // Duration of the animation (e.g., 500ms)
        final Duration duration = Duration.millis(500);
        // Create a new timeline for the animation
        final Timeline timeline = new Timeline();

        // KeyValue defines a value at a specific point in time for a specific property
        // Here, we're saying "at the end of the animation, set the progress property to 1.0"
        KeyValue keyValue = new KeyValue(progressBar.progressProperty(), 1.0);

        // KeyFrame defines a specific point in the timeline
        // Here, we're saying "at the end of the duration, apply the keyValue"
        KeyFrame keyFrame = new KeyFrame(duration, keyValue);

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }


    /**
     * Loads and displays the help window within the main application window.
     * This method uses JavaFX's FXMLLoader to load the help window from an FXML file.
     * It clears the existing content in the main window and adds the help window's content.
     * The help window is anchored to the right side of the main window.
     */
    @FXML
    public void loadHelp() {
        try {
            FXMLLoader helpLoad = new FXMLLoader(getClass().getResource("/fxml/help_window.fxml"));
            Parent helpViewParent = helpLoad.load();

            // TODO maybe take out of function and put into something you can call for all loaders
            mainWindow.getChildren().clear();

            mainWindow.getChildren().add(helpViewParent);
            AnchorPane.setRightAnchor(helpViewParent, 0d);
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Loads the graph display.
     */
    public void loadGraphs() {
        try {
            FXMLLoader loadGraphs = new FXMLLoader(getClass()
                    .getResource("/fxml/graph_window.fxml"));
            Parent graphsViewParent = loadGraphs.load();

            mainWindow.getChildren().clear();
            mainWindow.getChildren().add(graphsViewParent);
            AnchorPane.setRightAnchor(graphsViewParent, 0d);
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Loads menu display in FXML file into menuDisplayPane.
     */
    private void loadMenuDisplayFromFxml(String filePath) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(filePath));
        try {
            StackPane menuDisplay = loader.load();
            menuDisplayPane.getChildren().setAll(menuDisplay);
            if (!menuPopulated.equals("empty") && !menuPopulated.equals("import")) {
                controller = loader.getController();
            }

        } catch (IOException ioException) {
            log.error(ioException);
        }
    }

    /**
     * Toggles the menu display dependent on the button clicked.
     */
    public void toggleMenuDisplay(ActionEvent event) {
        Button menuButton = (Button) event.getSource();
        String menuChoice = (String) menuButton.getUserData();

        if (!menuPopulated.equals("empty") && !menuPopulated.equals("import")) {
            controller.updateManager();
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
        }

    }

    private void fadeOutLoadingScreen() {
        FadeTransition fadeTransition = new FadeTransition();

        // Set the node you want to fade out
        fadeTransition.setNode(loadingScreen);

        // Set the duration of the fade out
        fadeTransition.setDuration(Duration.millis(1000)); // 1 second, adjust as needed

        // Set the starting and ending opacity values
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        fadeTransition.setDelay(Duration.millis(1500));

        // Add an action to be processed once the animation is done
        fadeTransition.setOnFinished(event -> {
            loadingScreen.setVisible(false);
        });

        // Start the fade out
        fadeTransition.play();
    }

    public void quitApp() {
        Platform.exit();
    }

    @Override
    public void mapLoaded() {

        System.out.println("Hello");
        fadeOutLoadingScreen();
    }
}