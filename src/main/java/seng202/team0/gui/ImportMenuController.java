package seng202.team0.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import seng202.team0.business.DataManager;
import seng202.team0.repository.DatabaseManager;

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
        DataManager.getInstance().importFile(file);
    }

    public void resetDatabase() {
        DatabaseManager manager = DatabaseManager.getInstance();
        manager.resetDb();
    }


}
