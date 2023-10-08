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
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * Controller for the main.fxml window
 *
 * @author Team10
 */


public class MainController {

    private static final Logger log = LogManager.getLogger(MainController.class);
    @FXML
    private WebView webView;
    @FXML
    private StackPane mainWindow;
    private Stage stage;
    private WebEngine webEngine;
    public static JSObject javaScriptConnector;
    private MapController mapController;
    @FXML
    private AnchorPane menuDisplayPane;
    private String menuPopulated = "empty";
    private MenuController controller;


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

        webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    // if javascript loads successfully
                    if (newState == Worker.State.SUCCEEDED) {
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
                    }
                });

        mapController = new MapController();
        mapController.setWebView(webView);
        mapController.init(stage);

        loadMenuDisplayFromFXML("/fxml/empty_menu.fxml");
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

    public void loadGraphs() {
        try {
            FXMLLoader graphsLoader = new FXMLLoader(getClass().getResource("/fxml/graph_window.fxml"));
            Parent graphsViewParent = graphsLoader.load();

            mainWindow.getChildren().clear();
            mainWindow.getChildren().add(graphsViewParent);
            AnchorPane.setRightAnchor(graphsViewParent, 0d);
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Loads menu display in FXML file into menuDisplayPane
     */
    private void loadMenuDisplayFromFXML(String filePath) {
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
            loadMenuDisplayFromFXML("/fxml/empty_menu.fxml");

        } else if (Objects.equals("routing", menuChoice)) {
            menuPopulated = menuChoice;
            loadMenuDisplayFromFXML("/fxml/routing_menu.fxml");

        } else if (Objects.equals("filtering", menuChoice)) {
            menuPopulated = menuChoice;
            loadMenuDisplayFromFXML("/fxml/filtering_menu.fxml");

        } else if (Objects.equals("settings", menuChoice)) {
            menuPopulated = menuChoice;
            loadMenuDisplayFromFXML("/fxml/settings_menu.fxml");

        } else if (Objects.equals("import", menuChoice)) {
            menuPopulated = menuChoice;
            loadMenuDisplayFromFXML("/fxml/import_window.fxml");
        }

    }
}