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
     * This method is responsible for creating a circle on a map,
     * calculating its bounding box,
     * querying crash data from a database,
     * and updating the UI with the results.
     */
    @FXML
    private void createCircle() {

        String address = startRadius;
        Pair<Location, String> startResult = geolocator.getLocation(address);
        double circleRadius = radiusSlider.getValue();
        Location startMarker = startResult.getKey();

        RatingAreaManager ratingAreaManager = RatingAreaManager.getInstance();
        ratingAreaManager.setBoundingCircleCentre(startMarker.getLatitude(),
                startMarker.getLongitude());
        Location circleCentre = ratingAreaManager.getBoundingCircleCentre();

        MainController.javaScriptConnector.call("drawCircle", startMarker.getLatitude(),
                startMarker.getLongitude(), circleRadius);
        circleRadius = circleRadius * 0.08;
        String boundingWhere = null;
        if (circleCentre != null) {
            // Bounding box to lessen the load
            boundingWhere = "minX >= " + (circleCentre.getLongitude() - circleRadius)
                    + " AND maxX <= " + (circleCentre.getLongitude() + circleRadius)
                    + " AND minY >= " + (circleCentre.getLatitude() - circleRadius)
                    + " AND maxY <= " + (circleCentre.getLatitude() + circleRadius) + ")";

            // Pythagoras theorem calculation compared to circle radius
            boundingWhere += " AND (SQRT(POW(" + circleCentre.getLongitude()
                    + " - longitude, 2) + POW(" + circleCentre.getLatitude()
                    + " - latitude, 2)) <= " + circleRadius + ")";
        }
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
        }



    }

    /**
     * This method is responsible for creating a rectangle on a map,
     * calculating its bounding box, querying crash data from a database,
     * and updating the UI with the results.
     */
    @FXML
    public void createRectangle() {
        String address = startRadius;
        Pair<Location, String> startResult = geolocator.getLocation(address);
        double circleRadius = radiusSlider.getValue();
        Location startMarker = startResult.getKey();
        RatingAreaManager ratingAreaManager = RatingAreaManager.getInstance();

        MainController.javaScriptConnector.call("drawRectangle",
                startMarker.getLatitude(), startMarker.getLongitude(), circleRadius);

        double radiusLat = circleRadius / 111;
        double radiusLong = circleRadius / (111 * Math.cos(startMarker.getLatitude()
                * Math.PI / 180));

        double minLat = startMarker.getLatitude() - radiusLat;
        double maxLat = startMarker.getLatitude()  + radiusLat;
        double minLong = startMarker.getLongitude() - radiusLong;
        double maxLong = startMarker.getLongitude() + radiusLong;
        ratingAreaManager.setBoundingBoxMin(minLat, minLong);
        ratingAreaManager.setBoundingBoxMax(maxLat, maxLong);
        ratingAreaManager.setBoundingCircleCentre(
                startMarker.getLatitude(), startMarker.getLongitude());
        Location circleCentre = ratingAreaManager.getBoundingCircleCentre();
        Location boxMin = ratingAreaManager.getBoundingBoxMin();
        Location boxMax = ratingAreaManager.getBoundingBoxMax();
        String boundingWhere = null;

        if (boxMin != null || boxMax != null) {
            boundingWhere = "minX >= " + boxMin.getLongitude()
                    + " AND maxX <= " + boxMax.getLongitude()
                    + " AND minY >= " + boxMin.getLatitude()
                    + " AND maxY <= " + boxMax.getLatitude() + ")";

        } else if (circleCentre != null) {
            // Bounding box to lessen the load
            boundingWhere = "minX >= " + (circleCentre.getLongitude() - circleRadius)
                    + " AND maxX <= " + (circleCentre.getLongitude() + circleRadius)
                    + " AND minY >= " + (circleCentre.getLatitude() - circleRadius)
                    + " AND maxY <= " + (circleCentre.getLatitude() + circleRadius) + ")";

            // Pythagoras theorem calculation compared to circle radius
            boundingWhere += " AND (SQRT(POW(" + circleCentre.getLongitude()
                    + " - longitude, 2) + POW(" + circleCentre.getLatitude()
                    + " - latitude, 2)) <= " + circleRadius + ")";
        }
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
        }







    }

    /**
     * This method clears the drawing or layers on the map by calling a JavaScript function.
     */
    @FXML
    public void clearLayers() {
        MainController.javaScriptConnector.call("clearDrawing");


    }

}
