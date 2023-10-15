package seng202.team10.business;

import javafx.scene.control.CheckBox;

/**
 * Responsible for storing graph settings to be persistent.
 *
 */
public class GraphManager {
    private static GraphManager graphingManager;
    private String currentColumnData;
    private String currentColOfInterest;
    private Boolean currentAreFiltersTicked;
    private Boolean currentAreMapBoundsTicked;

    /**
     * Initializer of the GraphManager class that populates the graph settings
     * with the default state as pie graph of regions.
     */
    private GraphManager() {
        currentColumnData = "Regions";
        currentColOfInterest = "region";
        currentAreFiltersTicked = true;
        currentAreMapBoundsTicked = true;

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

    public String getCurrentColumnData() {
        return currentColumnData;
    }

    public void setCurrentColumnData(String newColumn) {
        currentColumnData = newColumn;
    }

    public String getCurrentColOfInterest() {
        return currentColOfInterest;
    }

    public void setCurrentColOfInterest(String newColumn) {
        currentColOfInterest = newColumn;
    }

    public boolean getCurrentAreFiltersTicked() {
        return currentAreFiltersTicked;
    }

    public void setCurrentAreFiltersTicked(boolean newTick) {
        currentAreFiltersTicked = newTick;
    }

    public boolean getCurrentAreMapBoundsTicked() {
        return currentAreMapBoundsTicked;
    }

    public void setCurrentAreMapBoundsTicked(boolean newTick) {
        currentAreMapBoundsTicked = newTick;
    }

}
