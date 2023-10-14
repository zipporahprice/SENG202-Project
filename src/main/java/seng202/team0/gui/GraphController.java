package seng202.team0.gui;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.App;
import seng202.team0.business.FilterManager;
import seng202.team0.business.GraphManager;
import seng202.team0.models.CrashSeverity;
import seng202.team0.repository.SqliteQueryBuilder;

/**
 * This class manages actions and views related to graphical representations of data.
 * It includes methods for initializing the window and handling navigation back to the main window.
 */
public class GraphController implements Initializable, MenuController {

    private static final Logger log = LogManager.getLogger(App.class);
    @FXML
    private PieChart pieChartMade;
    @FXML
    private AnchorPane pieChartPane;
    ObservableList<PieChart.Data> pieChartSqlTestData;

    private Stage stage;

    private static String columnOfInterest = "region";
    @FXML
    private ChoiceBox chartChoiceBox;
    private String currentChart = "Pie Graph";
    @FXML
    private ChoiceBox chartDataChoiceBox;
    @FXML
    private ComboBox chartDataComboBox;
    private static String currentChartData = "Region";
    @FXML
    private AnchorPane graphsDataPane;

    @FXML
    private Label holidayInfoLabel;
    @FXML
    private Label vehiclesInfoLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setChartOptions();
        setPieChartDataOptions();

        //TODO need to account for severity, transport type, year, holiday??
        pieChartSqlTestData = newPieChartData(columnOfInterest);

        setPieGraph(pieChartMade, pieChartSqlTestData);


    }


    /**
     * Update the data manager associated with the menu.
     */
    @Override
    public void updateManager() {
        GraphManager graphingManager = GraphManager.getInstance();
        graphingManager.setCurrentColumnData(currentChartData);
        graphingManager.setCurrentColOfInterest(columnOfInterest);

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

    }

    private void setPieGraph(PieChart pieGraph, ObservableList<PieChart.Data> pieData) {
        if (pieGraph.getData().size() != 0) {
            pieGraph.getData().clear();
            System.out.println("clearing data from pie graph. new: " + columnOfInterest);
        }

        for (PieChart.Data data : pieData) {
            pieGraph.getData().add(data);
        }

        pieGraph.setTitle("Crashes in Aotearoa by " + currentChartData);

        pieGraph.setLegendVisible(false);

        pieGraph.setLabelsVisible(true);
        pieGraph.setLabelLineLength(15);

        pieGraph.setMinSize(300, 300);
        pieGraph.setStartAngle(87);
        if (pieGraph.isVisible() == false) {
            System.out.println("PIE GRAPH NOT VISIBLE");
        }

        holidayInfoLabel.setVisible(false);
        vehiclesInfoLabel.setVisible(false);
        if (currentChartData.equals("Weather")) {
            pieGraph.setLegendVisible(true);
        } else if (currentChartData.equals("Holiday")) {
            holidayInfoLabel.setVisible(true);
        } else if (currentChartData.equals("Vehicle Type")) {
            vehiclesInfoLabel.setVisible(true);
        }

        int totalValue = pieGraph.getData().stream().mapToInt(data -> (int) data.getPieValue()).sum();


        pieGraph.getData().forEach(data -> {
            String percentage = String.format("%.2f%%", (data.getPieValue() / totalValue * 100));
            String count = String.valueOf((int) data.getPieValue());
            String slice = data.getName();
            Tooltip toolTipPercentRegion = new Tooltip(percentage + ", count: " + count + ", \n" + slice);
            Tooltip.install(data.getNode(), toolTipPercentRegion);
        });

    }

    private PieChart.Data newPieChartDataVehicleType(String vehicle, String columnWanted) {
//        ObservableList<PieChart.Data> result = FXCollections.observableArrayList();
        List<HashMap<Object, Object>> vehicleList = null;

        columnOfInterest = columnWanted;
        vehicleList = SqliteQueryBuilder.create()
                .select(columnOfInterest + ", COUNT(*)")
                .from("crashes")
                .groupBy(columnOfInterest)
                .build();

        ArrayList<String> sliceNames = new ArrayList<>();
        ArrayList<Double> sliceCounts = new ArrayList<>();

        System.out.println(vehicleList);

        for (HashMap<Object, Object> hash : vehicleList) {
            Object column = hash.get(columnOfInterest);
            double count = ((Number) hash.get("COUNT(*)")).doubleValue();
            System.out.println(column + " " + count);

            sliceNames.add(column.toString());
            sliceCounts.add(count);

            System.out.println(columnOfInterest + ": " + column + "  Count: " + count);
//            //TODO remove print statement
        }

        for (int i = 0; i < sliceNames.size(); i++) {
            String sliceName = sliceNames.get(i);
            if (sliceName.equals("Null")) {
                sliceName = "Unknown";
            } else if (sliceName.equals("1")) {
                sliceName = vehicle;
                PieChart.Data dataToAdd = new PieChart.Data(sliceName, sliceCounts.get(i));
                return dataToAdd;
            }

//            result.add(new PieChart.Data(sliceName, sliceCounts.get(i)));
        }

        log.error("Invalid vehicle type!");

//        System.out.println(result + "RESULTTTHJDHTSJ");


        return null;
    }

    private ObservableList<PieChart.Data> newPieChartData(String columnOfInterest) {
        ObservableList<PieChart.Data> result = FXCollections.observableArrayList();
        List<HashMap<Object, Object>> dbList = null;



        if (columnOfInterest.equals("truck_involved")) {
            //because truck is the last data item to be set and what columnOfInterest will be at the end.
            PieChart.Data bikeData =  newPieChartDataVehicleType("Bicycle", "bicycle_involved");
            PieChart.Data busData =  newPieChartDataVehicleType("Bus", "bus_involved");
            PieChart.Data carData =  newPieChartDataVehicleType("Car", "car_involved");
            PieChart.Data mopedData =  newPieChartDataVehicleType("Moped", "moped_involved");
            PieChart.Data motorcycleData =  newPieChartDataVehicleType("Motorcycle", "motorcycle_involved");
            PieChart.Data parkedData =  newPieChartDataVehicleType("Parked Vehicle", "parked_vehicle_involved");
            PieChart.Data pedestrianData =  newPieChartDataVehicleType("Pedestrian", "pedestrian_involved");
            PieChart.Data schoolBusData =  newPieChartDataVehicleType("School Bus", "school_bus_involved");
            PieChart.Data trainData =  newPieChartDataVehicleType("Train", "train_involved");
            PieChart.Data truckData =  newPieChartDataVehicleType("Truck", "truck_involved");

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

            return result;




//            columnOfInterest = "SUM(bicycle_involved) AS bicycle_count, " +
//                    "SUM(bus_involved) AS bus_count, " +
//                    "SUM(car_involved) AS car_count, " +
//                    "SUM(moped_involved) AS moped_count, " +
//                    "SUM(motorcycle_involved) AS motorcycle_count, " +
//                    "SUM(parked_vehicle_involved) AS parked_vehicle_count, " +
//                    "SUM(pedestrian_involved) AS pedestrian_count, " +
//                    "SUM(school_bus_involved) AS school_bus_count, " +
//                    "SUM(train_involved) AS train_count, " +
//                    "SUM(truck_involved) AS truck_count";

//            String columnOfInterestGroup = "bicycle_involved, " +
//                    "bus_involved, " +
//                    "car_involved, " +
//                    "moped_involved, " +
//                    "motorcycle_involved, " +
//                    "parked_vehicle_involved, " +
//                    "pedestrian_involved, " +
//                    "school_bus_involved, " +
//                    "train_involved, " +
//                    "truck_involved";

//            columnOfInterest = "bicycle_involved AS bicycle_count, " +
//                    "bus_involved AS bus_count, " +
//                    "car_involved AS car_count, " +
//                    "moped_involved AS moped_count, " +
//                    "motorcycle_involved AS motorcycle_count, " +
//                    "parked_vehicle_involved AS parked_vehicle_count, " +
//                    "pedestrian_involved AS pedestrian_count, " +
//                    "school_bus_involved AS school_bus_count, " +
//                    "train_involved AS train_count, " +
//                    "truck_involved AS truck_count";

//            columnOfInterest = "bicycle_involved bicycle_count, " +
//                    "bus_involved bus_count, " +
//                    "car_involved car_count, " +
//                    "moped_involved moped_count, " +
//                    "motorcycle_involved motorcycle_count, " +
//                    "parked_vehicle_involved parked_vehicle_count, " +
//                    "pedestrian_involved pedestrian_count, " +
//                    "school_bus_involved school_bus_count, " +
//                    "train_involved train_count, " +
//                    "truck_involved truck_count";




//            dbList = SqliteQueryBuilder.create()
//                    .select(columnOfInterestGroup + ", COUNT(*)")
//                    .from("crashes")
//                    .groupBy(columnOfInterestGroup)
//                    .build();
//
//            Map<String, Integer> vehicleTypeCounts = new HashMap<>();
//            ArrayList<String> vehicleTypes = new ArrayList<>();
//            vehicleTypes.add("bicycle_count");
//            vehicleTypes.add("bus_count");
//            vehicleTypes.add("car_count");
//            vehicleTypes.add("moped_count");
//            vehicleTypes.add("motorcycle_count");
//            vehicleTypes.add("parked_vehicle_count");
//            vehicleTypes.add("pedestrian_count");
//            vehicleTypes.add("school_bus_count");
//            vehicleTypes.add("train_count");
//            vehicleTypes.add("truck_count");
//
//            System.out.println(dbList);
//
//            for (HashMap<Object, Object> row : dbList) {
//                // Iterate over the vehicle type columns and update counts
//                for (String vehicleType : vehicleTypes) {
//                    int count = (int) row.get(vehicleType);
//                    vehicleTypeCounts.put(vehicleType, vehicleTypeCounts.getOrDefault(vehicleType, 0) + count);
//                }
//            }
//
//            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
//            for (String vehicleType : vehicleTypes) {
//                int count = vehicleTypeCounts.getOrDefault(vehicleType, 0);
//                System.out.println(vehicleType + " - vehicle type. count: " + count);
//                pieChartData.add(new PieChart.Data(vehicleType, count));
//            }
//
//            return pieChartData;

        } else {
            dbList = SqliteQueryBuilder.create()
                    .select(columnOfInterest + ", COUNT(*)")
                    .from("crashes")
                    .groupBy(columnOfInterest)
                    .build();
        }

        ArrayList<String> sliceNames = new ArrayList<>();
        ArrayList<Double> sliceCounts = new ArrayList<>();

        System.out.println(dbList);
        for (HashMap<Object, Object> hash1 : dbList) {
            Object column1 = hash1.get(columnOfInterest);
            double count1 = ((Number) hash1.get("COUNT(*)")).doubleValue();
            System.out.println(column1 + " +! " + count1);
        }

        for (HashMap<Object, Object> hash : dbList) {
            Object column = hash.get(columnOfInterest);
            double count = ((Number) hash.get("COUNT(*)")).doubleValue();
            System.out.println(column + " " + count);

            if (columnOfInterest.equals("severity")) {
                switch ((int) column) {
                    case 1:
                        column = "Non-injury";
                        break;
                    case 2:
                        column = "Minor";
                        break;
                    case 4:
                        column = "Serious";
                        break;
                    case 8:
                        column = "Fatal";
                        break;
                    default:
                        log.error("Invalid severity type");
                        break;
                }
            } else if (columnOfInterest.equals("holiday")) {
                switch ((int) column) {
                    case 0:
                        column = "Not a holiday";
                        break;
                    case 1:
                        column = "Was a holiday";
                        break;
                    default:
                        log.error("Invalid holiday type");
                        break;
                }
            }

            sliceNames.add(column.toString());
            sliceCounts.add(count);

            System.out.println(columnOfInterest + ": " + column + "  Count: " + count);
//            //TODO remove print statement
        }

        for (int i = 0; i < sliceNames.size(); i++) {
            String sliceName = sliceNames.get(i);
            if (sliceName.equals("Manawatū-Whanganui")) {
                sliceName = "Manawatu-Whanganui";
            } else if (sliceName.equals("Null")) {
                sliceName = "Unknown";
            }

            result.add(new PieChart.Data(sliceName, sliceCounts.get(i)));
        }

        return result;
    }

    /**
     * method to set chart options for dropdown, can only select 1 graph.
     */
    public void setChartOptions() {
        System.out.println("set chart options");
        chartChoiceBox.getItems().addAll("Pie Graph", "Line Graph");
        chartChoiceBox.setValue(currentChart);
        if (currentChart.equals("Pie Graph")) {
            graphsDataPane.setVisible(true);
            //todo set line graph pane visibility false
        } else {
            graphsDataPane.setVisible(false);
            //TODO set line graph data options
        }
        chartChoiceBox.getSelectionModel()
                .selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        currentChart = (String) newValue;
                    }
                    // Adjusted for the new option.
                    switch (currentChart) {
                        case "Pie Graph":
                            // Code to show the Pie Graph.
                            //TODO refactor newPieChartData into here.
                            graphsDataPane.setVisible(true);
                            //todo set line graph pane visibility false
                            break;
                        case "Line Graph":
                            // Code to show Line Graph.
                            graphsDataPane.setVisible(false);
                            //todo set line graph pane visibility true

                            break;
                        default:
                            // Other cases.
                            log.error("uh oh wrong choiceBox option");
                            break;
                    }
                });
    }

    /**
     * Dropdown choosing the data for pie graph.
     */
    public void setPieChartDataOptions() {
        chartDataComboBox.getItems().addAll(
                "Region", "Holiday", "Severity", "Vehicle Type", "Weather", "Year");
        chartDataComboBox.setValue(currentChartData);

//        chartDataChoiceBox.getItems().addAll(
//                "Region", "Holiday", "Severity", "Vehicle Type", "Weather", "Year");
//        chartDataChoiceBox.setValue(currentChartData);
        System.out.println(columnOfInterest + " COL OF INTEREST");
        chartDataComboBox.getSelectionModel()
                .selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        currentChartData = (String) newValue;
                    }
                    // Adjusted for the new option.
                    switch (currentChartData) {
                        case "Region":
                            columnOfInterest = "region";
                            break;
                        case "Holiday":
                            columnOfInterest = "holiday";
                            break;
                        case "Severity":
                            columnOfInterest = "severity";
                            break;
                        case "Vehicle Type":
                            //truck to trigger if statement in newPieChartData.
                            columnOfInterest = "truck_involved";
                            break;
                        case "Weather":
                            columnOfInterest = "weather";
                            break;
                        case "Year":
                            columnOfInterest = "crash_year";
                            break;
                        default:
                            // Other cases.
                            log.error("Invalid choiceBox option: " + currentChartData);
                            break;
                    }
                    log.info("Selected columnOfInterest: " + columnOfInterest);

                    ObservableList<PieChart.Data> newPieData = newPieChartData(columnOfInterest);
                    log.info("Data size: " + newPieData.size());
                    pieChartMade.getData().removeAll();
                    pieChartMade.setVisible(false);
                    setPieGraph(pieChartMade, newPieData);
                    pieChartMade.setVisible(true);
                });
    }


    /**
     * Initialize the window.
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        this.stage = stage;
    }



}
