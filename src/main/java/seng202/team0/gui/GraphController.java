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


//        pieGraph.setData(pieData);
        pieGraph.setTitle("Crashes in Aotearoa by " + columnOfInterest);
        pieGraph.setLegendVisible(false);
        pieGraph.setLabelsVisible(true);
        pieGraph.setLabelLineLength(30);
        if (pieGraph.isVisible() == false) {
            System.out.println("PIE GRAPH NOT VISIBLE");
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

        List<HashMap<String, Object>> dbList = SqliteQueryBuilder.create()
                .select(columnOfInterest + ", COUNT(*)")
                .from("crashes")
                .groupBy(columnOfInterest)
                .build();

        ArrayList<String> sliceNames = new ArrayList<>();
        ArrayList<Double> sliceCounts = new ArrayList<>();

        for (HashMap<String, Object> hash : dbList) {
            String column = (String) hash.get(columnOfInterest);
            double count = ((Number) hash.get("COUNT(*)")).doubleValue();

            sliceNames.add(column);
            sliceCounts.add(count);

            System.out.println(columnOfInterest + ": " + column + "  Count: " + count);
            //TODO remove print statement
        }

        for (int i = 0; i < sliceNames.size(); i++) {
            result.add(new PieChart.Data(sliceNames.get(i), sliceCounts.get(i)));
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
                "Region", "Severity", "Vehicle type", "Weather", "Year");
        chartDataChoiceBox.setValue(currentChartData);
//        columnOfInterest = ((String) chartDataChoiceBox.getValue()).toLowerCase();
        System.out.println(columnOfInterest + " COL OF INTEREST");
        chartDataChoiceBox.getSelectionModel()
                .selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        currentChartData = (String) newValue;
                    }
                    // Adjusted for the new option.
                    switch (currentChartData) {
                        case "Region":
                            // Code to show the regions pie chart.
                            columnOfInterest = "region";
//                            setPieGraph(pieChartMade, newPieChartData(columnOfInterest));


                            break;
                        case "Severity":
                            // Code to show severity pie chart.
//                            columnOfInterest = "severity";
                            break;
                        case "Vehicle type":
                            //code to show vehicle type pie chart.
                            break;
                        case "Weather":
                            //code to show weather pie chart
                            columnOfInterest = "weather";
//                            setPieGraph(pieChartMade, newPieChartData(columnOfInterest));
//                            pieChartMade.setVisible(true);

                            break;
                        case "Year":
                            //code to show the year data pie chart
//                            columnOfInterest = "crash-year";
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
