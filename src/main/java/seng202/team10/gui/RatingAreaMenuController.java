package seng202.team10.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import seng202.team10.business.FilterManager;
import seng202.team10.business.RatingAreaManager;
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
        String boundingWhere = RatingAreaManager.getInstance().rateAreaHelper();

        // If a bounding area exists, then query in to get rating
        if (boundingWhere != null) {

            double score = RatingAreaManager.queryHelper(boundingWhere).getFirst();
            int total = RatingAreaManager.queryHelper(boundingWhere).getSecond();
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
}
