package seng202.team10.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import seng202.team10.exceptions.DataImportException;
import seng202.team10.repository.DatabaseManager;

/**
 * Class to initialize the menu controller.
 *
 * @author team10
 */
public class ImportMenuController implements Initializable {

    @FXML
    private Button importDataButton;

    @FXML
    private Button resetDataButton;

    private PopOverController popOver = new PopOverController();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Opens the window to choose a file.
     * Has correct error handling
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
            popOver.showNotificationOnButtonPress(importDataButton, "Needs to be a CSV File");
            return;
        }

        try {
            DatabaseManager.getInstance().importFile(file);
        } catch (DataImportException e) {
            popOver.showNotificationOnButtonPress(importDataButton, "Import Error");
        } catch (Exception e) {
            // Catching other unexpected exceptions and notifying the user.
            popOver.showNotificationOnButtonPress(resetDataButton, String.valueOf(e));
        }
    }
    public void resetDatabase() {
        DatabaseManager manager = DatabaseManager.getInstance();
        manager.resetDb();
    }


}
