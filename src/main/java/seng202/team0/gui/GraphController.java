package seng202.team0.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
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
    private static String currentChartData = "Region";
    @FXML
    private AnchorPane graphsDataPane;


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

        if (currentChartData.equals("Weather")) {
            pieGraph.setLegendVisible(true);
        }


        pieGraph.getData().forEach(data -> {
            String percentage = String.format("%.2f%%", (data.getPieValue() / 100));
            String slice = data.getName();
            Tooltip toolTipPercentRegion = new Tooltip(percentage + ", " + slice);
            Tooltip.install(data.getNode(), toolTipPercentRegion);
        });

    }


    private ObservableList<PieChart.Data> newPieChartData(String columnOfInterest) {
        ObservableList<PieChart.Data> result = FXCollections.observableArrayList();
        List<HashMap<Object, Object>> dbList;


        if (columnOfInterest.equals("bicycle_involved")) {
            columnOfInterest = "SUM(bicycle_involved) AS bicycleCount, "
                    + "SUM(bus_involved) AS busCount, "
                    + "SUM(car_involved) AS carCount, "
                    + "SUM(moped_involved) AS mopedCount, "
                    + "SUM(motorcycle_involved) AS motorcycleCount, "
                    + "SUM(parked_vehicle_involved) AS pVehicleCount, "
                    + "SUM(pedestrian_involved) AS pedestrianCount, "
                    + "SUM(school_bus_involved) AS schoolBusCount, "
                    + "SUM(train_involved) AS trainCount, "
                    + "SUM(truck_involved) AS truckCount";


            dbList = SqliteQueryBuilder.create()
                    .select(columnOfInterest)
                    .from("crashes")
                    .build();
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
            System.out.println(column1 + " " + count1);
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
            }

            sliceNames.add(column.toString());
            sliceCounts.add(count);

            System.out.println(columnOfInterest + ": " + column + "  Count: " + count);
            //TODO remove print statement
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
        chartDataChoiceBox.getItems().addAll(
                "Region", "Severity", "Vehicle Type", "Weather", "Year");
        chartDataChoiceBox.setValue(currentChartData);
        System.out.println(columnOfInterest + " COL OF INTEREST");
        chartDataChoiceBox.getSelectionModel()
                .selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        currentChartData = (String) newValue;
                    }
                    // Adjusted for the new option.
                    switch (currentChartData) {
                        case "Region":
                            columnOfInterest = "region";
                            break;
                        case "Severity":
                            columnOfInterest = "severity";
                            break;
                        case "Vehicle Type":
                            //code to show vehicle type pie chart.
                            columnOfInterest = "bicycle_involved";
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
