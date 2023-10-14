package seng202.team10.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.*;
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

        try {
            DatabaseManager.getInstance().importFile(file);
        } catch (DataImportException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Import Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void resetDatabase() {
        DatabaseManager manager = DatabaseManager.getInstance();
        manager.resetDb();
    }


}
