package seng202.team10.business;

/**
 * Responsible for storing graph settings to be persistent.
 *
 */
public class GraphManager {
    private static GraphManager graphingManager;
    private String currentColumnData;
    private String currentColOfInterest;

    /**
     * Initializer of the GraphManager class that populates the graph settings
     * with the default state as pie graph of regions.
     */
    private GraphManager() {
        currentColumnData = "Regions";
        currentColOfInterest = "regions";
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

}
