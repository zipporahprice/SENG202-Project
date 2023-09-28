package seng202.team0.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class SettingsMenuController {

    @FXML
    private ChoiceBox viewChoiceBox;
    public static String currentView = "Automatic";

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
