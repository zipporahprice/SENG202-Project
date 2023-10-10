package seng202.team0.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import seng202.team0.business.IOManager;

/**
 * Class to initialize the menu controller.
 *
 * @author team10
 */
public class ImportMenuController implements Initializable {

    @FXML
    private Button importButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void openFileChooserDialog() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(importButton.getScene().getWindow());
        IOManager.getInstance().importFile(file);
    }


}
