package seng202.team0.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private Integer earliestYear;

    private List<String> modesSelected;
    private List<String> weathersSelected;
    private List<String> regionsSelected;

    private FilterManager() {
        severitiesSelected = new ArrayList<>();
        modesSelected = new ArrayList<>();
        weathersSelected = new ArrayList<>();
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

    public List<String> getWeathersSelected() { return this.weathersSelected; }
    public void addToWeathers(String weather) { weathersSelected.add(weather); }
    public void removeFromWeathers(String weather) { weathersSelected.remove((Object)weather); }

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

        if (getEarliestYear() != null) {
            where.add("crash_year >= " + getEarliestYear());
        }

        if (getWeathersSelected().size() > 0) {
            where.add("Weather IN (" +
                    getWeathersSelected().stream().map(weather -> "\""+weather+"\"").collect(Collectors.joining(", "))
                    + ")");
        }

        if (regionsSelected.size() > 0) {
            where.add("region IN(" +
                    getRegionsSelected().stream().map(region -> "\""+region+"\"").collect(Collectors.joining(", "))
                    + ")");

        }

        System.out.println(String.join(" AND ", where));


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
