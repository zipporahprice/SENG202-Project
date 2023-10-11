package seng202.team0.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import seng202.team0.business.FilterManager;
import seng202.team0.business.RatingAreaManager;
import seng202.team0.models.Location;
import seng202.team0.repository.SqliteQueryBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    public void rateArea() {
        String select = "severity";
        String from = "crashes";

        FilterManager filterManager = FilterManager.getInstance();
        String filterWhere = filterManager.toString();
        String[] filterList = filterWhere.split(" AND ");
        String filterWhereWithoutViewport = String.join(" AND ", Arrays.copyOf(filterList, filterList.length - 1));

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
            // Alert stuff here!
        }

    }
}
