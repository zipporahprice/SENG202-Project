package seng202.team0.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton class for storing filters from the FXML controller classes.
 * Stores filters in lists of the categories.
 *
 * @author Neil Alombro
 */

public class FilterManager {
    private static FilterManager filters;
    private List<Integer> severitiesSelected;

    private List<String> regionsSelected;
    private Integer earliestYear;

    private List<String> modesSelected;

    private FilterManager() {
        severitiesSelected = new ArrayList<>();
        modesSelected = new ArrayList<>();
        regionsSelected = new ArrayList<>();
    }

    public static FilterManager getInstance() {
        if (filters == null) {
            filters = new FilterManager();
        }
        return filters;
    }


    public List<Integer> getSeveritiesSelected() { return this.severitiesSelected; }

    public void addToSeverities(Integer severity) { severitiesSelected.add(severity); }

    public void removeFromSeverities(Integer severity) { severitiesSelected.remove((Object)severity); }
    public Integer getEarliestYear() { return earliestYear; }
    public void setEarliestYear(Integer year) { earliestYear = year; }

    @Override
    public String toString() {
        List<String> where = new ArrayList<>();

        if (getSeveritiesSelected().size() > 0) {
            where.add("severity IN (" +
                    getSeveritiesSelected().stream().map(Object::toString).collect(Collectors.joining(", "))
                    + ")");
        }

        if (filters.getModesSelected().size() > 0) {
            String modesCondition = filters.getModesSelected().stream().map(mode -> mode + " = 1").collect(Collectors.joining(" OR "));
            where.add(modesCondition);

        }

        if (regionsSelected.size() > 0) {
            where.add("region IN(" +
                    getRegionsSelected().stream().map(region -> "\""+region+"\"").collect(Collectors.joining(", "))
            + ")");

        }

        if (getEarliestYear() != null) {
            where.add("crash_year >= " + getEarliestYear());
        }

        return String.join(" AND ", where);
    }

    public List<String> getModesSelected() { return this.modesSelected; }

    public void addToModes(String mode) { modesSelected.add(mode); }

    public void removeFromModes(String mode) { modesSelected.remove((Object)mode); }

    public List<String> getRegionsSelected() { return this.regionsSelected; }

    public void addToRegions(String region) { regionsSelected.add(region); }

    public void removeFromRegions(String region) {
        regionsSelected.remove((Object)region);
    }
}
