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
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.animation.Animation;
import javafx.event.ActionEvent;


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

    private FadeTransition fadeTransition = new FadeTransition(Duration.millis(500));
    private FadeTransition[] emojiButtonTransitions = new FadeTransition[6];
    private boolean[] emojiButtonClicked = new boolean[6];  // Keep track of button states

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

    @FXML
    private void initialize() {
        setupEmojiButtonTransition(carButton, 0);
        setupEmojiButtonTransition(bikeButton, 1);
        setupEmojiButtonTransition(busButton, 2);
        setupEmojiButtonTransition(walkingButton, 3);
        setupEmojiButtonTransition(helicopterButton, 4);
        setupEmojiButtonTransition(motorbikeButton, 5);
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
        if (fadeTransition.getStatus() == Animation.Status.RUNNING) {
            fadeTransition.stop(); // Stop the animation if it's currently running
        }

        togglePaneWithFade(transportModePane, 0); // Pass an index to identify the pane
        togglePaneWithFade(weatherPane, 1);
        togglePaneWithFade(datePane, 2);
        togglePaneWithFade(boundariesPane, 3);
        togglePaneWithFade(severityPane, 4);

        // Play each fade animation individually
        for (int i = 0; i < 5; i++) {
            fadeTransitions[i].play();
        }
    }

    private FadeTransition[] fadeTransitions = new FadeTransition[5]; // Array to store individual fade transitions

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
        emojiButtonTransitions[index].setToValue(0.5);    // Semi-transparent
        emojiButtonTransitions[index].setOnFinished(event -> {
            if (emojiButtonClicked[index]) {
                button.getStyleClass().remove("clickedButtonColor");
            } else {
                button.getStyleClass().add("clickedButtonColor");
            }
            emojiButtonClicked[index] = !emojiButtonClicked[index];
        });
    }

    public void handleEmojiButtonClick(ActionEvent event) {
        Button button = (Button) event.getSource();
        System.out.println(button);
        int buttonIndex = Integer.parseInt(button.getUserData().toString());
        System.out.println("I was called with this line "+ buttonIndex);


        if (emojiButtonClicked[buttonIndex]) {
            emojiButtonTransitions[buttonIndex].setRate(-1);  // Reverse the animation when clicked again
        } else {
            emojiButtonTransitions[buttonIndex].setRate(1);
        }
        emojiButtonTransitions[buttonIndex].play();
    }






}
