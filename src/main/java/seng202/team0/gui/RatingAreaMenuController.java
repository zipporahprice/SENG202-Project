package seng202.team0.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import seng202.team0.business.FilterManager;
import seng202.team0.business.RatingAreaManager;
import seng202.team0.models.Location;
import seng202.team0.repository.SqliteQueryBuilder;

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
        String select = "severity";
        String from = "crashes";

        FilterManager filterManager = FilterManager.getInstance();
        String filterWhere = filterManager.toString();
        String[] filterList = filterWhere.split(" AND ");
        String filterWhereWithoutViewport = String.join(" AND ",
                Arrays.copyOf(filterList, filterList.length - 1));

        RatingAreaManager ratingAreaManager = RatingAreaManager.getInstance();
        Location boundingBoxMin = ratingAreaManager.getBoundingBoxMin();
        Location boundingBoxMax = ratingAreaManager.getBoundingBoxMax();

        if (boundingBoxMax != null && boundingBoxMin != null) {
            String where = "object_id IN (SELECT id FROM rtree_index WHERE minX >= "
                    + boundingBoxMin.getLongitude() + " AND maxX <= "
                    + boundingBoxMax.getLongitude() + " AND minY >= "
                    + boundingBoxMin.getLatitude() + " AND maxY <= "
                    + boundingBoxMax.getLatitude() + "))";

            List severityList = SqliteQueryBuilder
                    .create()
                    .select(select)
                    .from(from)
                    .where(filterWhereWithoutViewport + " AND " + where)
                    .build();

            int totalSeverity = 0;
            int total = 0;

            for (Object severityMap : severityList) {
                HashMap<String, Object> map = (HashMap<String, Object>) severityMap;
                totalSeverity += (int) map.get("severity");
                total += 1;
            }

            double averageSeverity = 0;
            if (total > 0) {
                averageSeverity = totalSeverity / total;
            }

            ratingAreaText.setText("Danger: " + String.format("%.2f", averageSeverity));
            numCrashesAreaLabel.setText("Number of crashes in area: " + total);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No bounding area drawn!"
                    + "\nPlease draw area before rating area.");

            alert.showAndWait();
        }

    }
}
