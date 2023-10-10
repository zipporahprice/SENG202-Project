package seng202.team0.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import seng202.team0.business.FilterManager;
import seng202.team0.models.Location;
import seng202.team0.repository.SqliteQueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Filter;

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

        List severityList = SqliteQueryBuilder
                .create()
                .select(select)
                .from(from)
                .where(filterWhere)
                .build();

        int totalSeverity = 0;
        double total = 0;

        for (Object severityMap : severityList) {
            HashMap<String, Object> map = (HashMap<String, Object>) severityMap;
            totalSeverity += (int) map.get("severity");
            total += 1;
        }

        double averageSeverity = 0;
        if (total > 0) {
            averageSeverity = totalSeverity / total;
        }

        ratingAreaText.setText("Danger: " + averageSeverity);
        numCrashesAreaLabel.setText("Number of crashes in area: " + total);

    }
}
