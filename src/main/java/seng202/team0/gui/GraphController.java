package seng202.team0.gui;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Filter;

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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team0.App;
import seng202.team0.business.FilterManager;
import seng202.team0.models.Favourite;
import seng202.team0.repository.DatabaseManager;

/**
 * This class manages actions and views related to graphical representations of data.
 * It includes methods for initializing the window and handling navigation back to the main window.
 */
public class GraphController implements Initializable {

    private static final Logger log = LogManager.getLogger(App.class);
    @FXML
    public PieChart pieChartTest;

    private Stage stage;

    private FilterManager filters = FilterManager.getInstance();
    private List<String> modesSelected = filters.getModesSelected();

    private Connection connection;
    private DatabaseManager databaseManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<PieChart.Data> pieChartTestData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Car", 20),
                        new PieChart.Data("Bike", 13),
                        new PieChart.Data("Bus", 17),
                        new PieChart.Data("Pedestrian", 25),
                        new PieChart.Data("Helicopter", 15),
                        new PieChart.Data("Truck", 10)
                );
        pieChartTest.setData(pieChartTestData);
        pieChartTest.setTitle("Test Pie Graph");

    }

    private ObservableList<PieChart.Data> newPieChart() {
        ObservableList<PieChart.Data> result = FXCollections.observableArrayList();

        String sqlQuery = "SELECT * FROM crashes"; //TODO figure out what query to do

        // Connect to database and gets the corresponding Favourite
        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new PieChart.Data(rs.getString(2), rs.getDouble(1)));
                    //TODO change columnIndexes, I have no idea what the table looks like.
                }
            }
        } catch (AssertionError | SQLException sqlException) {
            log.error(sqlException);
            return null;
        }

//        for (Object filter : filterList) {
//            double percentage = 0.0;
//            result.add(new PieChart.Data(filter.toString(), percentage));
//        }


        return result;
    }

    /**
     * Initialize the window.
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handles the action of navigating back to the main window view from the current view.
     *
     * @param event The ActionEvent that triggered this method.
     */
    public void handleBackButton(ActionEvent event) {
        try {
            // Load the main window FXML file
            FXMLLoader mainLoader = new FXMLLoader(getClass()
                    .getResource("/fxml/main.fxml"));
            Parent mainView = mainLoader.load();

            // Get the controller of main.fxml
            MainController mainController = mainLoader.getController();

            // Initialize the main window
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainController.init(window);

            // Create a new scene and put the main window into it
            Scene mainViewScene = new Scene(mainView);

            // Set the scene to the stage
            window.setScene(mainViewScene);

            // Finally, show the stage
            window.show();

        } catch (IOException e) {
            log.error(e);
        }
    }


}
