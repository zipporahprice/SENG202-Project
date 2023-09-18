package seng202.team0.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.event.ActionEvent;

public class helpController {

    @FXML
    public StackPane helpWindow;

    private Stage stage;

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        this.stage = stage;
    }

    public void handleBackButtonn() {
        try {
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent mainViewParent = mainLoader.load();

            MainController mainController = mainLoader.getController();
            mainController.init(stage);

            // Clearing the help window and loading the main window
            helpWindow.getChildren().clear();
            helpWindow.getChildren().add(mainViewParent);
            AnchorPane.setRightAnchor(mainViewParent, 0d);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Function to handle back navigation
    public void handleBackButton(ActionEvent event) {
        try {
            // Load the main window FXML file
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml")); // path to your main.fxml
            Parent mainView = mainLoader.load();

            // Get the controller of main.fxml
            MainController mainController = mainLoader.getController();

            // Initialize your main window
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainController.init(window); // Assuming your init method sets up everything including the map

            // Create a new scene and put the main window into it
            Scene mainViewScene = new Scene(mainView);

            // Set the scene to the stage
            window.setScene(mainViewScene);

            // Finally, show the stage
            window.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
