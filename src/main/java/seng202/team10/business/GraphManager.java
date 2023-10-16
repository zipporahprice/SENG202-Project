package seng202.team10.business;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import seng202.team10.repository.SqliteQueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    public boolean getCurrentNoCrashes() {
        return noCrashes;
    }

    public void setCurrentNoCrashes(boolean newCrashes) {
        noCrashes = newCrashes;
    }

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

    public String getFiltersWithoutViewport(String[] filterList) {
        String result = String.join(" AND ",
                Arrays.copyOf(filterList, filterList.length - 4));

        return result;
    }

    public String getViewportWithoutFilters(String[] filterList) {
        String result = String.join(" AND ",
                Arrays.copyOfRange(filterList, filterList.length - 4, filterList.length));

        return result;
    }

    public PieChart.Data addRelevantPieChartData(ArrayList<String> sliceNames, ArrayList<Double> sliceCounts, String vehicle) {
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
