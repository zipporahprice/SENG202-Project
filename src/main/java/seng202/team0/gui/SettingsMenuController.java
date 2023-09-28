package seng202.team0.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsMenuController implements Initializable {

    @FXML
    private ChoiceBox viewChoiceBox;
    public static String currentView = "Automatic";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setViewOptions();
    }

    public void setViewOptions() {
        viewChoiceBox.getItems().addAll("Automatic", "Heatmap", "Crash Locations");
        viewChoiceBox.setValue("Automatic");
        viewChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentView = (String) newValue;
            }
        });
    }

//    public String getCurrentView() { return (String) viewChoiceBox.getValue(); }
}
