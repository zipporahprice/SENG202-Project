package seng202.team10.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team10.App;
import seng202.team10.business.FilterManager;
import seng202.team10.business.GraphManager;
import seng202.team10.repository.SqliteQueryBuilder;


/**
 * This class manages actions and views related to graphical representations of data.
 * It includes methods for showing the pie graph and changing the data shown.
 */

public class GraphController implements Initializable, MenuController {

    private static final Logger log = LogManager.getLogger(App.class);

    private ObservableList<PieChart.Data> pieChartSqlTestData;
    private static String columnOfInterest = "region";
    private static String currentChartData = "Region";
    private boolean areFiltersTicked = true;
    private boolean areMapBoundsTicked = true;
    private String currentChart = "Pie Graph"; //for initial state of the graph
    public static GraphController graphController;
    private boolean noCrashes = true;
    @FXML
    private PieChart pieChartMade;
    @FXML
    private ComboBox chartDataComboBox;
    @FXML
    private AnchorPane graphsDataPane;
    @FXML
    private Label holidayInfoLabel;
    @FXML
    private Label vehiclesInfoLabel;
    @FXML
    private CheckBox filtersCheckBox;
    @FXML
    private CheckBox mapBoundsCheckBox;
    @FXML
    private Label noPieGraphLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadManager();

        setPieChartDataOptions();
        pieChartSqlTestData = newPieChartData(columnOfInterest);
        setPieGraph(pieChartMade, pieChartSqlTestData);
        graphController = this;
    }

    /**
     * Update the data manager associated with the menu.
     */
    @Override
    public void updateManager() {
        GraphManager graphingManager = GraphManager.getInstance();
        graphingManager.setCurrentColumnData(currentChartData);
        graphingManager.setCurrentColOfInterest(columnOfInterest);

        areFiltersTicked = filtersCheckBox.isSelected();
        graphingManager.setCurrentAreFiltersTicked(areFiltersTicked);

        areMapBoundsTicked = mapBoundsCheckBox.isSelected();
        graphingManager.setCurrentAreMapBoundsTicked(areMapBoundsTicked);
    }

    /**
     * Load initial data and settings into the menu manager.
     */
    @Override
    public void loadManager() {
        GraphManager graphingManager = GraphManager.getInstance();

        String currentColumnData = graphingManager.getCurrentColumnData();
        currentChartData = currentColumnData;

        String currentColOfInterest = graphingManager.getCurrentColOfInterest();
        columnOfInterest = currentColOfInterest;

        boolean currentAreFiltersTicked = graphingManager.getCurrentAreFiltersTicked();
        areFiltersTicked = currentAreFiltersTicked;
        filtersCheckBox.setSelected(areFiltersTicked);

        boolean currentAreMapBoundsTicked = graphingManager.getCurrentAreMapBoundsTicked();
        areMapBoundsTicked = currentAreMapBoundsTicked;
        mapBoundsCheckBox.setSelected(areMapBoundsTicked);
    }

    private void toggleNoPieGraph() {
        pieChartMade.setVisible(false);
        pieChartMade.setLabelsVisible(false);
        pieChartMade.setLabelLineLength(0);
        pieChartMade.setTitle("");
        noPieGraphLabel.setVisible(true);

    }

    private void setPieGraph(PieChart pieGraph, ObservableList<PieChart.Data> pieData) {


        if (pieGraph.getData().size() != 0) { //removing any old data from the pie graph
            pieGraph.getData().clear();
        }

        //basic settings for the pie graph
        pieGraph.setLegendVisible(false);
        pieGraph.setLabelsVisible(true);
        pieGraph.setLabelLineLength(15);
        pieGraph.setMinSize(300, 300);
        pieGraph.setStartAngle(87);
        holidayInfoLabel.setVisible(false);
        vehiclesInfoLabel.setVisible(false);
        noPieGraphLabel.setVisible(false);


        for (PieChart.Data data : pieData) {
            pieGraph.getData().add(data);
        } //adding new data to the pie graph

        pieGraph.setTitle("Crashes in Aotearoa by " + currentChartData);


        if (currentChartData.equals("Weather")) {
            pieGraph.setLegendVisible(true); //because a couple of slices too small to see
        } else if (currentChartData.equals("Holiday")) {
            holidayInfoLabel.setVisible(true);
        } else if (currentChartData.equals("Vehicle Type")) {
            vehiclesInfoLabel.setVisible(true);
        }

        setTooltipInfo(pieGraph); //sets informative tooltips for each slice

        if (pieGraph.getData().size() == 0
                || (noCrashes == true && currentChartData.equals("Vehicle Type"))) {
            toggleNoPieGraph();

        }
    }

    private void setTooltipInfo(PieChart pieGraph) {
        //getting the total number of points of interest to calculate percentage
        int totalValue = pieGraph.getData()
                .stream().mapToInt(data -> (int) data.getPieValue()).sum();

        //creating the tooltip in the format "*percentage*, *number of points*, *slice name*"
        pieGraph.getData().forEach(data -> {
            String percentage = String.format("%.2f%%", (data.getPieValue() / totalValue * 100));
            String count = String.valueOf((int) data.getPieValue());
            String slice = data.getName();
            Tooltip toolTipPercentRegion = new Tooltip(percentage + ", count: "
                    + count + ", \n" + slice);
            Tooltip.install(data.getNode(), toolTipPercentRegion);
        });
    }

    private List<?> getPieChartData() {
        String where = FilterManager.getInstance().toString();
        String[] filterList = where.split(" AND ");

        // Takes away the 4 ANDS that make up the viewport
        // bounds we do not want in our query.
        String filtersWithoutViewport = String.join(" AND ",
                Arrays.copyOf(filterList, filterList.length - 4));

        // Gets the filtering string with just the viewport
        String viewportWithoutFilters = String.join(" AND ",
                Arrays.copyOfRange(filterList, filterList.length - 4, filterList.length));

        String finalWhere = "";

        if (filtersCheckBox.isSelected()) {
            finalWhere += filtersWithoutViewport;
        }

        if (mapBoundsCheckBox.isSelected()) {
            if (finalWhere.equals("")) {
                finalWhere += viewportWithoutFilters;
            } else {
                finalWhere += " AND " + viewportWithoutFilters;
            }
        }

        if (finalWhere.isEmpty()) {
            return SqliteQueryBuilder.create()
                    .select(columnOfInterest + ", COUNT(*)")
                    .from("crashes")
                    .groupBy(columnOfInterest)
                    .buildGetter();
        } else {
            return SqliteQueryBuilder.create()
                    .select(columnOfInterest + ", COUNT(*)")
                    .from("crashes")
                    .where(finalWhere)
                    .groupBy(columnOfInterest)
                    .buildGetter();
        }
    }

    private PieChart.Data createVehiclePieData(String vehicle, String columnWanted) {
        columnOfInterest = columnWanted;

        List<?> vehicleList = getPieChartData(); //to hold the result of the sql query

        ArrayList<String> sliceNames = new ArrayList<>();
        ArrayList<Double> sliceCounts = new ArrayList<>();

        //extracting slice value and count
        for (Object hash : vehicleList) {
            HashMap<Object, Object> vehicleHashMap = (HashMap<Object, Object>) hash;
            Object column = vehicleHashMap.get(columnOfInterest);
            double count = ((Number) vehicleHashMap.get("COUNT(*)")).doubleValue();
            sliceNames.add(column.toString());
            sliceCounts.add(count);
            if (count > 0) {
                noCrashes = false;
            }
        }

        //adding pie chart data only if the vehicle was involved i.e. sliceName = 1
        for (int i = 0; i < sliceNames.size(); i++) {
            String sliceName = sliceNames.get(i);
            if (sliceName.equals("1")) {
                sliceName = vehicle;
                PieChart.Data dataToAdd = new PieChart.Data(sliceName, sliceCounts.get(i));
                return dataToAdd;
            }

        }

        return new PieChart.Data("", 0); //in case there is no vehicle involved
    }

    private ObservableList<PieChart.Data> newPieChartVehicleData() {
        ObservableList<PieChart.Data> result = FXCollections.observableArrayList();
        noCrashes = true;

        //avoid complex SQL query by creating PieChart.Data elements
        // by vehicle type to add to result
        PieChart.Data bikeData = createVehiclePieData("Bicycle", "bicycle_involved");
        PieChart.Data busData = createVehiclePieData("Bus", "bus_involved");
        PieChart.Data carData = createVehiclePieData("Car", "car_involved");
        PieChart.Data mopedData = createVehiclePieData("Moped", "moped_involved");
        PieChart.Data motorcycleData = createVehiclePieData("Motorcycle", "motorcycle_involved");
        PieChart.Data parkedData = createVehiclePieData("Parked Vehicle",
                "parked_vehicle_involved");
        PieChart.Data pedestrianData = createVehiclePieData("Pedestrian", "pedestrian_involved");
        PieChart.Data schoolBusData = createVehiclePieData("School Bus", "school_bus_involved");
        PieChart.Data trainData = createVehiclePieData("Train", "train_involved");
        PieChart.Data truckData = createVehiclePieData("Truck", "truck_involved");

        result.add(bikeData);
        result.add(busData);
        result.add(carData);
        result.add(mopedData);
        result.add(motorcycleData);
        result.add(parkedData);
        result.add(pedestrianData);
        result.add(schoolBusData);
        result.add(trainData);
        result.add(truckData);

        if (result.size() == 0 || noCrashes == true) {
            toggleNoPieGraph();
        }

        return result;
    }

    private ObservableList<PieChart.Data> newPieChartData(String columnOfInterest) {
        ObservableList<PieChart.Data> result = FXCollections.observableArrayList();

        if (currentChartData.equals("Vehicle Type")) {
            result = newPieChartVehicleData();
            return result;
        }

        //querying the database for the column and count
        List<?> dbList = getPieChartData();

        ArrayList<String> sliceNames = new ArrayList<>();
        ArrayList<Double> sliceCounts = new ArrayList<>();

        //looping through to extract slice name and corresponding count
        for (Object hash : dbList) {
            HashMap<Object, Object> hashMap = (HashMap<Object, Object>) hash;
            Object column = hashMap.get(columnOfInterest);
            double count = ((Number) hashMap.get("COUNT(*)")).doubleValue();

            //creating more informative slice names
            if (columnOfInterest.equals("severity")) {
                switch ((int) column) {
                    case 1 -> column = "Non-injury";
                    case 4 -> column = "Minor";
                    case 16 -> column = "Serious";
                    case 64 -> column = "Fatal";
                    default -> log.error("Invalid severity type");
                }
            } else if (columnOfInterest.equals("holiday")) {
                switch ((int) column) {
                    case 0 -> column = "Not a holiday";
                    case 1 -> column = "Was a holiday";
                    default -> log.error("Invalid holiday type");
                }
            }

            sliceNames.add(column.toString());
            sliceCounts.add(count);

        }

        //adding sliceName and sliceCount to result
        for (int i = 0; i < sliceNames.size(); i++) {
            String sliceName = sliceNames.get(i);
            //handling edge cases
            if (sliceName.equals("Manawatū-Whanganui")) {
                sliceName = "Manawatu-Whanganui"; //todo figure out macron
            } else if (sliceName.equals("Null")) {
                sliceName = "Unknown";
            }

            result.add(new PieChart.Data(sliceName, sliceCounts.get(i)));
        }

        if (result.size() == 0) {
            toggleNoPieGraph();
        }

        return result;
    }


    /**
     * Dropdown choosing the data for pie graph.
     */
    public void setPieChartDataOptions() {
        //setting up the comboBox for chartData
        chartDataComboBox.getItems().addAll(
                "Region", "Holiday", "Severity", "Vehicle Type", "Weather", "Year");
        chartDataComboBox.setValue(currentChartData);

        chartDataComboBox.getSelectionModel()
                .selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        currentChartData = (String) newValue;
                    }
                    // Adjusted for the newly selected option using enhanced switch.
                    switch (currentChartData) {
                        case "Region" -> columnOfInterest = "region";
                        case "Holiday" -> columnOfInterest = "holiday";
                        case "Severity" -> columnOfInterest = "severity";
                        case "Vehicle Type" -> //truck to trigger if statement in newPieChartData.
                                columnOfInterest = "truck_involved";
                        case "Weather" -> columnOfInterest = "weather";
                        case "Year" -> columnOfInterest = "crash_year";
                        default -> // Other cases.
                                log.error("Invalid comboBox option: " + currentChartData);
                    }

                    updateGraph();
                });
    }

    /**
     * Takes in two lists of Pie Chart data.
     *
     * @param list1 first list of pie chart data
     * @param list2 second list of pie chart data
     * @return boolean of if the lists are identical
     */
    private boolean arePieChartDataListsIdentical(ObservableList<PieChart.Data> list1, ObservableList<PieChart.Data> list2) {
        if (list1.size() != list2.size()) {
            return false; // Different sizes, not identical.
        }

        for (int i = 0; i < list1.size(); i++) {
            PieChart.Data data1 = list1.get(i);
            PieChart.Data data2 = list2.get(i);

            if (!data1.getName().equals(data2.getName()) || Math.abs(data1.getPieValue() - data2.getPieValue()) > 0.001) {
                return false; // Found a difference, not identical.
            }
        }

        return true; // Lists are identical.
    }

    /**
     * Updates the graph showing with the selected columnOfInterest.
     */
    @FXML
    public void updateGraph() {
        ObservableList<PieChart.Data> newPieData = newPieChartData(columnOfInterest);

        currentChartData = (String) chartDataComboBox.getValue();

        ObservableList<PieChart.Data> pieChartDataInController = graphController.pieChartMade.getData();
        if (!arePieChartDataListsIdentical(newPieData, pieChartDataInController)) {
            pieChartMade.setVisible(false);
            setPieGraph(pieChartMade, newPieData); //updating the pie graph w new data
            if (newPieData.size() == 0) {
                toggleNoPieGraph();
                pieChartMade.setVisible(false);
            } else {
                pieChartMade.setVisible(true);
            }
        } else {
            log.info("Graphing: Identical information!");
        }
    }
}

//todo continue bug fixing

//    @FXML
//    public void updateGraph() {
//        ObservableList<PieChart.Data> newPieData;
//        currentChartData = (String) chartDataComboBox.getValue();
//        System.out.println("CURRENT CHART DATA : " + currentChartData);
//
//        if (currentChartData.equals("Vehicle Type")) {
//            // When "Vehicle Type" is selected, use the special handling
//            System.out.println("VEHICLE TYPE  SELECTED");
//            newPieData = newPieChartVehicleData();
//        } else {
//            // For other options, apply filters if necessary
//            newPieData = applyFiltersIfNeeded(newPieChartData(columnOfInterest));
//        }
//
//        pieChartMade.getData().clear(); // Clear the old data
//        pieChartMade.setVisible(false);
//        setPieGraph(pieChartMade, newPieData); // Update the pie graph with new data
//        if (newPieData.size() == 0) {
//            toggleNoPieGraph();
//            pieChartMade.setVisible(false);
//        } else {
//            pieChartMade.setVisible(true);
//        }
//    }
//
//    private ObservableList<PieChart.Data> applyFiltersIfNeeded(
//          ObservableList<PieChart.Data> data) {
//        if (areFiltersTicked || areMapBoundsTicked) {
//            pieChartMade.setVisible(false);
//            List<?> filteredData = getPieChartData();
//
//            // Process filteredData if needed
//
//            // Return the processed data
//            return processFilteredData(data, filteredData);
//        }
//
//        return data;
//    }
//
//    private ObservableList<PieChart.Data> processFilteredData(
//          ObservableList<PieChart.Data> data, List<?> filteredData) {
//        // Process the filtered data and return the updated data
//        // Implement your logic to apply filters here
//        // This depends on your specific requirements and database structure
//        return data;
//    }
//}