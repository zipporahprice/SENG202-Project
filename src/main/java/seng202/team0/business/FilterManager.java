package seng202.team0.business;

import seng202.team0.models.CrashSeverity;
import seng202.team0.models.Region;
import seng202.team0.models.Weather;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Singleton class for storing filters from the FXML controller classes.
 * Stores filters in lists of the categories.
 *
 * @author Neil Alombro
 */

// TODO currently has "null" taking care of no checkboxes ticked that will return no points. Works but bad

public class FilterManager {
    private static FilterManager filters;
    private List<Integer> severitiesSelected;
    private Integer earliestYear;

    private List<String> modesSelected;
    private List<String> weathersSelected;
    private List<String> regionsSelected;

    private HashMap<String, String> startOfClauses = new HashMap<String, String>() {{
        put("severity", "severity IN (");
        put("transport_mode", "(");
        put("crash_year", "crash_year >= ");
        put("weather", "weather IN (");
        put("region", "region IN (");
    }};

    private FilterManager() {
        severitiesSelected = new ArrayList<>(
                Arrays.stream(CrashSeverity.values()).map(severity -> severity.getValue()).toList()
        );
        modesSelected = new ArrayList<>(Arrays.asList(
                "bicycle_involved",
                "bus_involved",
                "car_involved",
                "moped_involved",
                "motorcycle_involved",
                "parked_vehicle_involved",
                "pedestrian_involved",
                "school_bus_involved",
                "train_involved",
                "truck_involved"
        ));
        weathersSelected = new ArrayList<>(
                Arrays.stream(Weather.values()).map(weather -> weather.getName()).toList()
        );
        regionsSelected = new ArrayList<>(
                Arrays.stream(Region.values()).map(region -> region.getName()).toList()
        );
    }

    public static FilterManager getInstance() {
        if (filters == null) {
            filters = new FilterManager();
        }
        return filters;
    }


    public List<Integer> getSeveritiesSelected() { return this.severitiesSelected; }

    public void addToSeverities(Integer severity) { severitiesSelected.add(severity); }

    public void removeFromSeverities(Integer severity) { severitiesSelected.remove(severity); }
    public Integer getEarliestYear() { return earliestYear; }
    public void setEarliestYear(Integer year) { earliestYear = year; }

    public List<String> getWeathersSelected() { return this.weathersSelected; }
    public void addToWeathers(String weather) { weathersSelected.add(weather); }
    public void removeFromWeathers(String weather) { weathersSelected.remove((Object)weather); }
    public List<String> getModesSelected() { return this.modesSelected; }

    public void addToModes(String mode) { modesSelected.add(mode); }

    public void removeFromModes(String mode) { modesSelected.remove(mode); }

    public List<String> getRegionsSelected() { return this.regionsSelected; }

    public void addToRegions(String region) { regionsSelected.add(region); }

    public void removeFromRegions(String region) { regionsSelected.remove(region); }

    public void updateFiltersWithQueryString(String query) {
        // Restarts FilterManager
        filters = new FilterManager();

        // Clear all lists
        severitiesSelected = new ArrayList<>();
        modesSelected = new ArrayList<>();
        weathersSelected = new ArrayList<>();
        regionsSelected = new ArrayList<>();

        if (!Objects.equals(query, "1 = 0")) {
            String[] queryList = query.split(" AND ");

            // TODO look out for quotation marks and make a substring from 1 index to length - 1 to get rid of quotation marks

            for (String filter : queryList) {
                if (filter.startsWith(startOfClauses.get("severity"))) {
                    String severitiesString = filter.substring(startOfClauses.get("severity").length(), filter.length() - 1);
                    Arrays.stream(severitiesString.split(", ")).forEach(severityString ->
                            severitiesSelected.add(Integer.parseInt(severityString)));
                } else if (filter.startsWith(startOfClauses.get("transport_mode"))) {
                    String transportModesString = filter.substring(startOfClauses.get("transport_mode").length(), filter.length() - 1);
                    System.out.println(transportModesString.split(" OR "));
                    Arrays.stream(transportModesString.split(" OR ")).forEach(transportModeString ->
                            modesSelected.add(transportModeString));
                } else if (filter.startsWith(startOfClauses.get("crash_year"))) {
                    earliestYear = Integer.parseInt(filter.substring(startOfClauses.get("crash_year").length()));
                } else if (filter.startsWith(startOfClauses.get("weather"))) {
                    String weathersString = filter.substring(startOfClauses.get("weather").length(), filter.length() - 1);
                    Arrays.stream(weathersString.split(", ")).forEach(weatherString ->
                            weathersSelected.add(weatherString.substring(1, weatherString.length() - 1)));
                } else if (filter.startsWith(startOfClauses.get("region"))) {
                    String regionsString = filter.substring(startOfClauses.get("region").length(), filter.length() - 1);
                    Arrays.stream(regionsString.split(", ")).forEach(regionString ->
                            regionsSelected.add(regionString.substring(1, regionString.length() - 1)));
                }
            }
        }
    }

    @Override
    public String toString() {
        List<String> where = new ArrayList<>();

        if (getSeveritiesSelected().size() > 0) {
            where.add(startOfClauses.get("severity") +
                    getSeveritiesSelected().stream().map(Object::toString).collect(Collectors.joining(", "))
                    + ")");
        }

        if (filters.getModesSelected().size() > 0) {
            String modesCondition = filters.getModesSelected().stream().map(mode -> mode + " = 1").collect(Collectors.joining(" OR "));
            where.add(startOfClauses.get("transport_mode") + modesCondition + ")");

        }

        if (getEarliestYear() != null) {
            where.add(startOfClauses.get("crash_year") + getEarliestYear());
        }

        if (getWeathersSelected().size() > 0) {
            where.add(startOfClauses.get("weather") +
                    getWeathersSelected().stream().map(weather -> "\""+weather+"\"").collect(Collectors.joining(", "))
                    + ")");
        }

        if (regionsSelected.size() > 0) {
            where.add(startOfClauses.get("region") +
                    getRegionsSelected().stream().map(region -> "\""+region+"\"").collect(Collectors.joining(", "))
                    + ")");
        }

        // TODO hacking the database with always false to return no rows, CHANGE TO SOMETHING BETTER
        if (modesSelected.size() == 0 || severitiesSelected.size() == 0 ||
                weathersSelected.size() == 0 || regionsSelected.size() == 0) {
            return "1 = 0";
        } else {
            return String.join(" AND ", where);
        }

    }

}
