package seng202.team0.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import seng202.team0.business.SettingsManager;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsMenuController implements Initializable, MenuController {

    @FXML
    private ChoiceBox viewChoiceBox;
    public static String currentView = "Automatic";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setViewOptions();
    }

    public void setViewOptions() {
        viewChoiceBox.getItems().addAll("Automatic", "Heatmap", "Crash Locations");
        viewChoiceBox.setValue(currentView);// viewChoiceBox.setValue("Automatic");
        viewChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentView = (String) newValue;
            }
        });
    }

    @Override
    public void updateManager() {
        SettingsManager settings = SettingsManager.getInstance();
        settings.setCurrentView(currentView);
    }

    @Override
    public void loadManager() {
        SettingsManager settings = SettingsManager.getInstance();

        String view = settings.getCurrentView();
        currentView = view;
    }

//    public String getCurrentView() { return (String) viewChoiceBox.getValue(); }
}
