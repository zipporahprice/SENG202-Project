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
            String select = "AVG(severity), COUNT()";
            String from = "crashes";

            FilterManager filterManager = FilterManager.getInstance();
            String filterWhere = filterManager.toString();
            String[] filterList = filterWhere.split(" AND ");

            // Takes away the 4 ANDS that make up the viewport
            // bounds we do not want in our query.
            String filterWhereWithoutViewport = String.join(" AND ",
                    Arrays.copyOf(filterList, filterList.length - 4));

            String rtreeFind = "object_id IN (SELECT id FROM rtree_index WHERE " + boundingWhere;

            List severityList = SqliteQueryBuilder
                    .create()
                    .select(select)
                    .from(from)
                    .where(filterWhereWithoutViewport + " AND " + rtreeFind)
                    .buildGetter();

            HashMap<String, Object> resultHashMap = (HashMap) severityList.get(0);

            // Calculates the score based on the query result
            double score = 0.0;
            int total = 0;
            if (resultHashMap.get("AVG(severity)") != null) {
                double averageSeverity = (double) resultHashMap.get("AVG(severity)");
                total = (int) resultHashMap.get("COUNT()");

                if (total > 0) {
                    // Actual average severity will range from 1 to 8
                    // Score rating massaged to be out of 10 and in a range from 0 to 10.
                    double scaleFactor = 10.0 / Math.log(11.0);
                    score = Math.log(averageSeverity + 1) * scaleFactor;
                    score = Math.min(10, score);
                }
            }

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
