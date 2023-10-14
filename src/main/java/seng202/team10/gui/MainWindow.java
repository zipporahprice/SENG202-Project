package seng202.team10.gui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;


/**
 * Class starts the javaFX application window.
 *
 * @author seng202 teaching team
 */
public class MainWindow extends Application {

    /**
     * Opens the gui with the fxml content specified in resources/fxml/main.fxml
     *
     * @param primaryStage The current fxml stage, handled by javaFX Application class
     * @throws IOException if there is an issue loading fxml file
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = baseLoader.load();

        //sets the screen size to full screen windowed.
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double estimatedTitleBarHeight = 30;
        Scene scene = new Scene(root, screenBounds.getWidth(),
                screenBounds.getHeight() - estimatedTitleBarHeight);

        primaryStage.setScene(scene);
        MainController baseController = baseLoader.getController();
        baseController.init(primaryStage);

        primaryStage.setTitle("SafeTrip");
        primaryStage.show();
    }


    /**
     * Launches the FXML application, this must be called from
     * another class (in this case App.java) otherwise JavaFX
     * errors out and does not run
     *
     * @param args command line arguments
     */
    public static void main(String [] args) {
        launch(args);
    }

}
