package seng202.team0.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.services.CounterService;


/**
 * Controller for the main.fxml window
 * @author seng202 teaching team
 */

// TODO hacked date picker with traslate x and y to get it to the places it needs to be

public class MainController {

    private static final Logger log = LogManager.getLogger(MainController.class);

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

    private CounterService counterService;



    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        stage.setMaximized(true);
        counterService = new CounterService();
    }

    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onButtonClicked() {
        log.info("Button has been clicked");
        counterService.incrementCounter();

        int count = counterService.getCurrentCount();
        defaultLabel.setText(Integer.toString(count));
    }

    public void toggleHamburger() {
        transportModePane.setVisible(!transportModePane.isVisible());
        weatherPane.setVisible(!weatherPane.isVisible());
        datePane.setVisible(!datePane.isVisible());
        boundariesPane.setVisible(!boundariesPane.isVisible());
        severityPane.setVisible(!severityPane.isVisible());
    }
}
