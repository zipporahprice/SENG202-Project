package seng202.team10.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.util.Pair;
import seng202.team10.business.FilterManager;
import seng202.team10.business.RatingAreaManager;
import seng202.team10.models.GeoLocator;
import seng202.team10.models.Location;
import seng202.team10.repository.SqliteQueryBuilder;

/**
 * The RatingAreaMenuController class is a controller responsible for managing
 * the user interface for rating a specific area based on crash severities and
 * a defined bounding box.
 * Implements the MenuController interface.
 */
public class RatingAreaMenuController implements MenuController {
    @FXML
    public Label ratingAreaText;
    @FXML
    public Label numCrashesAreaLabel;

    @FXML
    private Label radiusText;

    @FXML
    private Slider radiusSlider;

    @FXML
    private ComboBox startLocation;

    private String startRadius;
    GeoLocator geolocator = new GeoLocator();




    @Override
    public void updateManager() {

    }

    @Override
    public void loadManager() {

    }

    /**
     * rates the area based on severity and crashes.
     */
    public void rateArea() {
        RatingAreaManager ratingAreaManager = RatingAreaManager.getInstance();
        String boundingWhere = ratingAreaManager.rateAreaHelper();

        // If a bounding area exists, then query in to get rating
        if (boundingWhere != null) {

            double score = ratingAreaManager.queryHelper(boundingWhere).getFirst();
            int total = ratingAreaManager.queryHelper(boundingWhere).getSecond();
            // Changes the visual cues with colour of area on map and text within info box.
            MainController.javaScriptConnector.call("changeDrawingColourToRating", score);
            ratingAreaText.setText("Danger: "
                    + String.format("%.2f", score) + " / 10");
            numCrashesAreaLabel.setText("Number of crashes in area: " + total);
        } else {
            // Shows alert if bounding area does not exist.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No bounding area drawn!"
                    + "\nPlease draw area before rating area.");

            alert.showAndWait();
        }
    }

    /**
     * This public method updates the slider value and displays it in the radiusText component.
     */
    @FXML
    public void updateSlider() {

        int startSliderValue = (int) Math.round(radiusSlider.getValue());


        radiusText.setText("Radius: " + Integer.toString(startSliderValue) + " km");

    }

    /**
     * This private method loads start location options into the
     * startLocation combo box based on user input.
     */
    @FXML
    private void loadStartOptions() {
        String address = startLocation.getEditor().getText().trim();

        ObservableList<String> addressOptions = geolocator.getAddressOptions(address);
        startLocation.setItems(addressOptions);
        startLocation.getEditor().setText(address);
    }

    /**
     * This private method sets the startRadius variable based
     * on the selected item in the startLocation combo box.
     */
    @FXML
    private void setStart() {
        String selectedItem = (String) startLocation.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            startRadius = selectedItem;
        }
    }

    /**
     * This method takes the user to their entered location.
     */
    @FXML
    public void panToLocation() {
        String address = startRadius;
        Pair<Location, String> startResult = geolocator.getLocation(address);

        Location startMarker = startResult.getKey();

        MainController.javaScriptConnector
                .call("panToLocation", startMarker.getLatitude(), startMarker.getLongitude());


    }

}
