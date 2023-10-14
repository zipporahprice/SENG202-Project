package seng202.team10.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import seng202.team10.repository.DatabaseManager;
import seng202.team10.exceptions.DataImportException;

/**
 * Class to initialize the menu controller.
 *
 * @author team10
 */
public class ImportMenuController implements Initializable {

    @FXML
    private Button importDataButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Opens the window to choose a file.
     *
     */
    public void openFileChooserDialog() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(importDataButton.getScene().getWindow());

        if (file == null) {
            // User canceled the file chooser, exit the method.
            return;
        }

        if (!file.getName().endsWith(".csv")) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Import Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid file format. Please import a CSV file.");
            alert.showAndWait();
            return;
        }

        try {
            DatabaseManager.getInstance().importFile(file);
        } catch (DataImportException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Import Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            // Catching other unexpected exceptions and notifying the user.
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Import Error");
            alert.setHeaderText(null);
            alert.setContentText("An unexpected error occurred while importing the file.");
            alert.showAndWait();
        }
    }

    public void resetDatabase() {
        DatabaseManager manager = DatabaseManager.getInstance();
        manager.resetDb();
    }


}
