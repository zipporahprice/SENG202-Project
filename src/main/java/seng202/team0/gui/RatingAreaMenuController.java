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
        RatingAreaManager ratingAreaManager = RatingAreaManager.getInstance();
        Location boxMin = ratingAreaManager.getBoundingBoxMin();
        Location boxMax = ratingAreaManager.getBoundingBoxMax();
        Location circleCentre = ratingAreaManager.getBoundingCircleCentre();
        double circleRadius = ratingAreaManager.getBoundingCircleRadius();

        String where = null;
        if (boxMin != null || boxMax != null) {
            where = "minX >= " + boxMin.getLongitude()
                    + " AND maxX <= " + boxMax.getLongitude()
                    + " AND minY >= " + boxMin.getLatitude()
                    + " AND maxY <= " + boxMax.getLatitude() + ")";

        } else if (circleCentre != null) {
            // Bounding box to lessen the load
            where = "minX >= " + (circleCentre.getLongitude() - circleRadius)
                    + " AND maxX <= " + (circleCentre.getLongitude() + circleRadius)
                    + " AND minY >= " + (circleCentre.getLatitude() - circleRadius)
                    + " AND maxY <= " + (circleCentre.getLatitude() + circleRadius) + ")";

            // Pythagoras theorem calculation compared to circle radius
            where += " AND (SQRT(POW(" + circleCentre.getLongitude()
                    + " - longitude, 2) + POW(" + circleCentre.getLatitude()
                    + " - latitude, 2)) <= " + circleRadius + ")";
        }

        double start = System.currentTimeMillis();

        if (where != null) {
            String select = "AVG(severity), COUNT()";
            String from = "crashes";

            FilterManager filterManager = FilterManager.getInstance();
            String filterWhere = filterManager.toString();
            String[] filterList = filterWhere.split(" AND ");

            // 4 ANDS to take away to get rid of the viewport
            String filterWhereWithoutViewport = String.join(" AND ",
                    Arrays.copyOf(filterList, filterList.length - 4));

            String rtreeFind = "object_id IN (SELECT id FROM rtree_index WHERE " + where;

            List severityList = SqliteQueryBuilder
                    .create()
                    .select(select)
                    .from(from)
                    .where(filterWhereWithoutViewport + " AND " + rtreeFind)
                    .buildGetter();

            HashMap<String, Object> resultHashMap = (HashMap) severityList.get(0);

            double score = 0.0;
            int total = 0;
            if (resultHashMap.get("AVG(severity)") != null) {
                double averageSeverity = (double) resultHashMap.get("AVG(severity)");
                total = (int) resultHashMap.get("COUNT()");

                if (total > 0) {
                    // Actual average severity will range from 1 to 8
                    // Score rating massaged to be out of 10 and in a range from 0 to 10.
                    score = ((averageSeverity - 1.0) / 7.0) * 10;
                }
            }

            MainController.javaScriptConnector.call("changeDrawingColourToRating", score);
            ratingAreaText.setText("Danger: "
                    + String.format("%.2f", score) + " / 10");
            numCrashesAreaLabel.setText("Number of crashes in area: " + total);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No bounding area drawn!"
                    + "\nPlease draw area before rating area.");

            alert.showAndWait();
        }
        double end = System.currentTimeMillis();

        System.out.println(end - start);


    }
}
