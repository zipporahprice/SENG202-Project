package seng202.team10.gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import seng202.team10.business.SettingsManager;

/**
 * The `SettingsMenuController` class is responsible for managing user interactions
 * with settings in the application. It implements the
 * `Initializable` and `MenuController` interfaces to handle initialization and updates
 * of view settings. This class allows our users to select a view option from a ChoiceBox
 * and updates the application's settings accordingly.
 *
 * @author Team 10
 */
public class SettingsMenuController implements Initializable, MenuController {

    @FXML
    private ChoiceBox viewChoiceBox;
    public static String currentView = "None";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setViewOptions();
    }

    /**
     * Populates the view choice options in the ChoiceBox
     * Available options include
     * "None," "Automatic," "Heatmap," "Crash Locations," and "Heatmap & Crash Locations."
     */
    public void setViewOptions() {
        viewChoiceBox.getItems().addAll("None", "Automatic", "Heatmap", "Crash Locations",
                "Heatmap & Crash Locations");
        viewChoiceBox.setValue(currentView);
        viewChoiceBox.getSelectionModel()
                .selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        currentView = (String) newValue;
                        MainController.javaScriptConnector.call("updateView");
                    } //updates the view based on the option selected
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
    //todo look at this
}
