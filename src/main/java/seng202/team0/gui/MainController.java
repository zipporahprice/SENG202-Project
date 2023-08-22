package seng202.team0.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.gui.MapController;

import java.io.IOException;

/**
 * Controller for the main.fxml window
 * @author seng202 teaching team
 */


public class MainController {

    private static final Logger log = LogManager.getLogger(MainController.class);
    public WebView webView;

    @FXML
    private AnchorPane mainWindow;
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
    private AnchorPane datePane;
    @FXML
    private AnchorPane boundariesPane;
    @FXML
    private AnchorPane severityPane;


    private Stage stage;






    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    void init(Stage stage) {
        stage.setMaximized(true);
        loadMap(stage);
        stage.sizeToScene();
    }



    public void loadMap(Stage stage) {
        try {
            FXMLLoader webViewLoader = new FXMLLoader(getClass().getResource("/fxml/map.fxml"));
            Parent mapViewParent = webViewLoader.load();

            MapController mapViewController = webViewLoader.getController();
            mapViewController.init(stage);

            mainWindow.getChildren().add(mapViewParent);
            AnchorPane.setRightAnchor(mapViewParent,0d);



        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void toggleHamburger() {
        transportModePane.setVisible(!transportModePane.isVisible());
        weatherPane.setVisible(!weatherPane.isVisible());
        datePane.setVisible(!datePane.isVisible());
        boundariesPane.setVisible(!boundariesPane.isVisible());
        severityPane.setVisible(!severityPane.isVisible());
    }
}
