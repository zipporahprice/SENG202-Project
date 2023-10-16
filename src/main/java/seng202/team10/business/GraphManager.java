package seng202.team10.business;



import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;
import java.util.ArrayList;
import java.util.Arrays;


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
    private Boolean noCrashes = true;

    /**
     * Initializer of the GraphManager class that populates the graph settings
     * with the default state as pie graph of regions.
     */
    private GraphManager() {
        currentColumnData = "Region";
        currentColOfInterest = "region";
        currentAreFiltersTicked = true;
        currentAreMapBoundsTicked = true;
        noCrashes = true;

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

    /**
     * Retrieves the current column data.
     *
     * @return the current column data as a String.
     */
    public String getCurrentColumnData() {
        return currentColumnData;
    }

    /**
     * Updates the value of currentColumnData.
     *
     * @param newColumn the new column data.
     */
    public void setCurrentColumnData(String newColumn) {
        currentColumnData = newColumn;
    }

    /**
     * Retrieves the current column of interest.
     *
     * @return the current column of interest as a String.
     */
    public String getCurrentColOfInterest() {
        return currentColOfInterest;
    }

    /**
     * Updates the value of currentColOfInterest.
     *
     * @param newColumn the new column of interest.
     */
    public void setCurrentColOfInterest(String newColumn) {
        currentColOfInterest = newColumn;
    }

    /**
     * Checks whether the filters are ticked.
     *
     * @return true if the filters are ticked, false otherwise.
     */
    public boolean getCurrentAreFiltersTicked() {
        return currentAreFiltersTicked;
    }

    /**
     * Sets the ticked status of filters.
     *
     * @param newTick true to tick the filters, false to untick them.
     */
    public void setCurrentAreFiltersTicked(boolean newTick) {
        currentAreFiltersTicked = newTick;
    }

    /**
     * Checks whether the map bounds are ticked.
     *
     * @return true if the map bounds are ticked, false otherwise.
     */
    public boolean getCurrentAreMapBoundsTicked() {
        return currentAreMapBoundsTicked;
    }

    /**
     * Sets the ticked status of map bounds.
     *
     * @param newTick true to tick the map bounds, false to untick them.
     */
    public void setCurrentAreMapBoundsTicked(boolean newTick) {
        currentAreMapBoundsTicked = newTick;
    }

    /**
     * Checks whether there are no crashes.
     *
     * @return true if there are no crashes, false otherwise.
     */
    public boolean getCurrentNoCrashes() {
        return noCrashes;
    }

    /**
     * Sets the status of crashes.
     *
     * @param newCrashes true to indicate there are no crashes, false to indicate there are crashes.
     */
    public void setCurrentNoCrashes(boolean newCrashes) {
        noCrashes = newCrashes;
    }

    /**
     * Sets tooltips for a PieChart.
     *
     * @param pieGraph the PieChart on which tooltips are to be set.
     * @return the modified PieChart with tooltips set.
     */
    public PieChart setTooltipInfo(PieChart pieGraph) {
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

        return pieGraph;
    }

    /**
     * Gets filters without viewport.
     *
     * @param filterList list containing filters.
     * @return a String representing filters without viewport.
     */
    public String getFiltersWithoutViewport(String[] filterList) {
        String result = String.join(" AND ",
                Arrays.copyOf(filterList, filterList.length - 4));

        return result;
    }

    /**
     * Gets viewport without filters.
     *
     * @param filterList list containing filters.
     * @return a String representing viewport without filters.
     */
    public String getViewportWithoutFilters(String[] filterList) {
        String result = String.join(" AND ",
                Arrays.copyOfRange(filterList, filterList.length - 4, filterList.length));

        return result;
    }

    /**
     * Adds relevant PieChart data based on vehicle involvement.
     *
     * @param sliceNames  list containing slice names.
     * @param sliceCounts list containing slice counts.
     * @param vehicle     the vehicle of interest.
     * @return a PieChart.Data object containing relevant data.
     */
    public PieChart.Data addRelevantPieChartData(ArrayList<String> sliceNames
            , ArrayList<Double> sliceCounts, String vehicle) {
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


}
