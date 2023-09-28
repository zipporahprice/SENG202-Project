package seng202.team0.gui;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.Objects;

/**
 * Controller for the main.fxml window
 * @author Team10
 */


public class MainController {

    private static final Logger log = LogManager.getLogger(MainController.class);
    public WebView webView;
    @FXML
    private StackPane mainWindow;
    private Stage stage;
    private WebEngine webEngine;
    public static JSObject javaScriptConnector;
    private MapController mapController;
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
        stage.setMaximized(true);
        mapController = new MapController();
        mapController.setWebView(webView);
        mapController.init(stage);

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
    private void loadRoutingMenuDisplay() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/routing_menu.fxml"));
        try {
            StackPane routingMenuDisplay = loader.load();
            menuDisplayPane.getChildren().setAll(routingMenuDisplay);
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
     * Toggles the menu display dependent on the button clicked.
     */
    public void toggleMenuDisplay(ActionEvent event) {
        Button menuButton = (Button) event.getSource();
        String menuChoice = (String) menuButton.getUserData();

        if (Objects.equals(menuPopulated, menuChoice)) {
            loadEmptyMenuDisplay();
            menuPopulated = "empty";
        } else if (Objects.equals("routing", menuChoice)) {
            loadRoutingMenuDisplay();
            menuPopulated = menuChoice;
        } else if (Objects.equals("filtering", menuChoice)) {
            loadFilteringMenuDisplay();
            menuPopulated = menuChoice;
        } else if (Objects.equals("settings", menuChoice)) {
            loadSettingsMenuDisplay();
            menuPopulated = menuChoice;
        }

    }
}