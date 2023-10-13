package seng202.team0.business;

import javafx.scene.chart.PieChart;
import seng202.team0.gui.GraphController;

/**
 * Responsible for storing graph settings from FXML to be consistent.
 *
 */
public class GraphManager {
    private static GraphManager graphingManager;

    private String currentColumn;

    /**
     * Initializer of the GraphManager class that populates the graph settings
     * with the default state as pie graph of regions.
     */
    private GraphManager() {
        currentColumn = "regions";
    }

    /**
     * Gets instance of or creates a new GraphManager.
     *
     * @return the graphingManager
     */
    public static GraphManager getInstance() {
        if (graphingManager == null) {
            graphingManager = new GraphManager();
        }

        return graphingManager;
    }


}
