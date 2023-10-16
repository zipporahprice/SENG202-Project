package seng202.team10.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.scene.control.CheckBox;
import seng202.team10.models.CrashSeverity;
import seng202.team10.models.Location;
import seng202.team10.models.Region;
import seng202.team10.models.Weather;


/**
 * Singleton class for storing filters from the FXML controller classes.
 * Stores filters in lists of the categories.
 *
 * @author Angelica Silva
 * @author Christopher Wareing
 * @author Neil Alombro
 * @author Todd Vermeir
 * @author William Thompson
 * @author Zipporah Price
 *
 */

public class FilterManager {

    // Connectors for changing FilterManager to a where clause string, and back
    private static final String and = " AND ";
    private static final String quote = "\"";
    private static final String or = " OR ";
    private static final String comma = ", ";
    private static final String closeParenthesis = ")";
    private static final String equalOne = " = 1";
    private final HashMap<String, String> startOfClauses = new HashMap<>() {{
            put("severity", "severity IN (");
            put("transport_mode", "(");
            put("crash_year", "crash_year BETWEEN ");
            put("weather", "weather IN (");
            put("region", "region IN (");
            put("holiday", "holiday IN (");
            put("viewport", "object_id IN (");
        }};
    private final String falseQuery = "1 = 0";

    // Singleton instance of FilterManager
    private static FilterManager filters;
    private List<Integer> severitiesSelected;
    private Integer earliestYear;
    private Integer latestYear;
    private List<String> modesSelected;
    private List<String> weathersSelected;
    private List<String> regionsSelected;
    private List<Integer> holidaysSelected;
    private Location viewPortMin;
    private Location viewPortMax;

    /**
     * Initializer of the FilterManager class that populates the filters
     * with the default beginning state with all option checkboxes selected
     * and included, and the earliestYear set to the earliest year of 2000.
     */
    private FilterManager() {
        severitiesSelected = new ArrayList<>(
                Arrays.stream(CrashSeverity.values()).map(CrashSeverity::getValue).toList()
        );

        earliestYear = 2000;

        latestYear = 2023;

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

        holidaysSelected = new ArrayList<>(Arrays.asList(0, 1));
    }


    /**
     * Retrieves the singleton instance of the FilterManager class.
     *
     * @return The singleton instance of FilterManager.
     */
    public static FilterManager getInstance() {
        if (filters == null) {
            filters = new FilterManager();
        }
        return filters;
    }


    /**
     * Retrieves the list of selected severity levels for filtering crash data.
     *
     * @return A list of selected severity levels.
     */
    public List<Integer> getSeveritiesSelected() {
        return this.severitiesSelected;
    }

    /**
     * Adds a severity level to the list of selected severity levels.
     *
     * @param severity The severity level to add.
     */
    public void addToSeverities(Integer severity) {
        severitiesSelected.add(severity);
    }

    /**
     * Removes a severity level from the list of selected severity levels.
     *
     * @param severity The severity level to remove.
     */
    public void removeFromSeverities(Integer severity) {
        severitiesSelected.remove(severity);
    }

    /**
     * Retrieves the earliest year for filtering crash data.
     *
     * @return The earliest year for filtering.
     */
    public Integer getEarliestYear() {
        return earliestYear;
    }

    public Integer getLatestYear() {
        return latestYear;
    }

    /**
     * Sets the earliest year for filtering crash data.
     *
     * @param year The earliest year to set.
     */
    public void setEarliestYear(Integer year) {
        earliestYear = year;
    }

    public void setLatestYear(Integer year) {
        latestYear = year;
    }

    /**
     * Retrieves the list of selected transportation modes for filtering crash data.
     *
     * @return A list of selected transportation modes.
     */
    public List<String> getModesSelected() {
        return this.modesSelected;
    }

    /**
     * Adds a transportation mode to the list of selected transportation modes.
     *
     * @param mode The transportation mode to add.
     */
    public void addToModes(String mode) {
        modesSelected.add(mode);
    }

    /**
     * Removes a transportation mode from the list of selected transportation modes.
     *
     * @param mode The transportation mode to remove.
     */
    public void removeFromModes(String mode) {
        modesSelected.remove(mode);
    }

    /**
     * Retrieves the list of selected weather conditions for filtering crash data.
     *
     * @return A list of selected weather conditions.
     */
    public List<String> getWeathersSelected() {
        return this.weathersSelected;
    }

    /**
     * Adds a weather condition to the list of selected weather conditions.
     *
     * @param weather The weather condition to add.
     */
    public void addToWeathers(String weather) {
        weathersSelected.add(weather);
    }

    /**
     * Removes a weather condition from the list of selected weather conditions.
     *
     * @param weather The weather condition to remove.
     */
    public void removeFromWeathers(String weather) {
        weathersSelected.remove((Object) weather);
    }

    /**
     * Retrieves the list of selected regions for filtering crash data.
     *
     * @return A list of selected regions.
     */
    public List<String> getRegionsSelected() {
        return this.regionsSelected;
    }

    /**
     * Adds a region to the list of selected regions.
     *
     * @param region The region to add.
     */
    public void addToRegions(String region) {
        regionsSelected.add(region);
    }

    /**
     * Removes a region from the list of selected regions.
     *
     * @param region The region to remove.
     */
    public void removeFromRegions(String region) {
        regionsSelected.remove(region);
    }

    /**
     * Retrieves the list of holidays (0 for No, 1 for Yes) for filtering crash data.
     *
     * @return A list of holidays (0 for No, 1 for Yes).
     */
    public List<Integer> getHolidaysSelected() {
        return this.holidaysSelected;
    }

    /**
     * Adds a holiday to the list of selected holidays (0 for No, 1 for Yes).
     *
     * @param holiday The holiday to add.
     */
    public void addToHolidays(int holiday) {
        holidaysSelected.add(holiday);
    }

    /**
     * Removes a holiday from the list of selected holidays (0 for No, 1 for Yes).
     *
     * @param holiday The holiday to remove.
     */
    public void removeFromHolidays(int holiday) {
        holidaysSelected.remove(holiday);
    }

    /**
     * Retrieves the location for the minimum point of the viewport for filtering crash data.
     *
     * @return A location of (minLatitude, minLongitude).
     */
    public Location getViewPortMin() {
        return this.viewPortMin;
    }

    public void updateEarliestYear(int newEarliestYear) {
        earliestYear = newEarliestYear;
    }

    /**
     * Sets the viewport minimum location for filtering crash data.
     *
     * @param minLatitude minimum latitude of viewport
     * @param minLongitude minimum longitude of viewport
     */
    public void setViewPortMin(double minLatitude, double minLongitude) {
        viewPortMin = new Location(minLatitude, minLongitude);
    }

    /**
     * Retrieves the location for the minimum point of the viewport for filtering crash data.
     *
     * @return A location of (maxLatitude, maxLongitude).
     */
    public Location getViewPortMax() {
        return this.viewPortMax;
    }

    /**
     * Sets the viewport maximum location for filtering crash data.
     *
     * @param maxLatitude maximum latitude of viewport
     * @param maxLongitude maximum longitude of viewport
     */
    public void setViewPortMax(double maxLatitude, double maxLongitude) {
        viewPortMax = new Location(maxLatitude, maxLongitude);
    }

    /**
     * Updates the filters based on a query string.
     * This method clears all existing filter lists and updates them with the filter values
     * extracted from the provided query string.
     *
     * @param query The query string containing filter values.
     */
    public void updateFiltersWithQueryString(String query) {
        // Clear all lists
        severitiesSelected.clear();
        modesSelected.clear();
        weathersSelected.clear();
        regionsSelected.clear();
        earliestYear = 2000;
        latestYear = 2023;
        holidaysSelected.clear();

        // If not the falseQuery, then iterate through and add
        // all the filters according to the given string.
        if (!Objects.equals(query, falseQuery)) {
            String[] queryList = query.split(and);

            for (String filter : queryList) {
                if (filter.startsWith(startOfClauses.get("severity"))) {
                    String severitiesString = filter.substring(startOfClauses
                            .get("severity").length(), filter.length() - 1);
                    Arrays.stream(severitiesString.split(comma)).forEach(severityString ->
                            addToSeverities(Integer.parseInt(severityString)));
                } else if (filter.startsWith(startOfClauses.get("transport_mode"))) {
                    String transportModesString = filter.substring(startOfClauses
                            .get("transport_mode").length(), filter.length() - 1);
                    Arrays.stream(transportModesString.split(or)).forEach(transportModeString ->
                            addToModes(transportModeString.split(" ")[0]));
                } else if (filter.startsWith(startOfClauses.get("crash_year"))) {
                    setEarliestYear(Integer.parseInt(filter.substring(startOfClauses
                            .get("crash_year").length())));
                } else if (filter.startsWith(startOfClauses.get("weather"))) {
                    String weathersString = filter.substring(startOfClauses
                            .get("weather").length(), filter.length() - 1);
                    Arrays.stream(weathersString.split(comma)).forEach(weatherString ->
                            addToWeathers(weatherString.substring(1, weatherString.length() - 1)));
                } else if (filter.startsWith(startOfClauses.get("region"))) {
                    String regionsString = filter.substring(startOfClauses
                            .get("region").length(), filter.length() - 1);
                    Arrays.stream(regionsString.split(comma)).forEach(regionString ->
                            addToRegions(regionString.substring(1, regionString.length() - 1)));
                } else if (filter.startsWith(startOfClauses.get("holiday"))) {
                    String holidaysString = filter.substring(startOfClauses
                            .get("holiday").length(), filter.length() - 1);
                    Arrays.stream(holidaysString.split(comma)).forEach(holidayString ->
                            addToHolidays(Integer.parseInt(holidayString)));
                }
            }
        }
    }


    /**
     * Generates a query string based on the selected filters for retrieving crash data.
     * This method constructs a query string based on the selected
     * severity levels, transportation modes,
     * earliest year, weather conditions, and regions.
     * The query string is used to filter crash data.
     *
     * @return A query string representing the selected filters for crash data retrieval.
     */
    @Override
    public String toString() {
        // NOTE: Missing 53645 due to transport mode having none of the subset we chose
        // and Chatham Islands. We are ok with this but to note when looking at
        // full database numbers and all crashes displayed since they will be different.

        List<String> where = new ArrayList<>();

        // If any severity selected, add to where clause
        if (getSeveritiesSelected().size() > 0) {
            where.add(startOfClauses.get("severity")
                    + getSeveritiesSelected().stream().map(Object::toString)
                    .collect(Collectors.joining(comma))
                    + closeParenthesis);
        }

        // If any transport mode selected, add to where clause
        if (filters.getModesSelected().size() > 0) {
            String modesCondition = filters.getModesSelected().stream()
                    .map(mode -> mode + equalOne).collect(Collectors.joining(or));
            where.add(startOfClauses.get("transport_mode") + modesCondition + closeParenthesis);

        }

        // If earliest and latest years are populated, add to where clause
        if (getEarliestYear() != null && getLatestYear() != null) {
            where.add(startOfClauses.get("crash_year")
                    + getEarliestYear()
                    + " AND "
                    + getLatestYear());
        }

        // If any weather selected, add to where clause
        if (getWeathersSelected().size() > 0) {
            where.add(startOfClauses.get("weather")
                    +
                    getWeathersSelected().stream().map(weather -> quote + weather + quote)
                            .collect(Collectors.joining(comma))
                    + closeParenthesis);
        }

        // If any region selected, add to where clause
        if (regionsSelected.size() > 0) {
            where.add(startOfClauses.get("region")
                    + getRegionsSelected().stream().map(region -> quote + region + quote)
                    .collect(Collectors.joining(comma))
                    + closeParenthesis);
        }

        // If a holiday choice (yes or no) is selected, add to where clause
        if (holidaysSelected.size() > 0) {
            where.add(startOfClauses.get("holiday")
                    + getHolidaysSelected().stream().map(Object::toString)
                    .collect(Collectors.joining(comma))
                    + closeParenthesis);
        }

        // If viewport bounds exist, add to where clause
        if (viewPortMin != null && viewPortMax != null) {
            where.add(startOfClauses.get("viewport") + "SELECT id FROM rtree_index WHERE minX >= "
                    + viewPortMin.getLongitude() + " AND maxX <= "
                    + viewPortMax.getLongitude() + " AND minY >= "
                    + viewPortMin.getLatitude() + " AND maxY <= "
                    + viewPortMax.getLatitude() + closeParenthesis);
        }

        // If any of the IN statements have empty sets,
        // then have the falseQuery make sure the result is empty.
        if (modesSelected.isEmpty() || severitiesSelected.isEmpty() || weathersSelected.isEmpty()
                || regionsSelected.isEmpty() || holidaysSelected.isEmpty()) {
            return falseQuery;
        } else {
            return String.join(and, where);
        }
    }

    /**
     * Takes a transport mode checkbox and checks if it has been selected.
     * Either adds or removes this transport mode to the FilterManager's list of modes.
     *
     * @param checkBox Checkbox representing the transport mode.
     */
    public static void addToTransport(CheckBox checkBox) {
        Object toAdd = checkBox.getUserData();
        if (checkBox.isSelected()) {
            if (!filters.getModesSelected().contains((String) toAdd)) {
                filters.addToModes((String) toAdd);
            }
        } else {
            filters.removeFromModes((String) toAdd);
        }
    }

    /**
     * Takes a weather checkbox and checks if it has been selected.
     * Either adds or removes this weather from the FilterManager's list of selections.
     *
     * @param checkBox Checkbox representing the weather type.
     */
    public static void addToWeather(CheckBox checkBox) {
        Object toAdd = checkBox.getUserData();
        if (checkBox.isSelected()) {
            if (!filters.getWeathersSelected().contains((String) toAdd)) {
                filters.addToWeathers((String) toAdd);
            }
        } else {
            filters.removeFromWeathers((String) toAdd);
        }
    }

    /**
     * Takes a severity checkbox and checks if it has been selected.
     * Either adds or removes this severity from the FilterManager's list of selections.
     *
     * @param checkBox Checkbox representing the given severity.
     */
    public static void addToSeverity(CheckBox checkBox) {
        Object toAdd = checkBox.getUserData();
        int severity = Integer.parseInt((String) toAdd);
        if (checkBox.isSelected()) {
            if (!filters.getSeveritiesSelected().contains(severity)) {
                filters.addToSeverities(severity);
            }
        } else {
            filters.removeFromSeverities(severity);
        }
    }

    /**
     * Takes a region checkbox and checks if it has been selected.
     * Either adds or removes this region from the FilterManager's list of selections.
     *
     * @param checkBox Checkbox representing the given region.
     */
    public static void addToRegion(CheckBox checkBox) {
        Object toAdd = checkBox.getUserData();
        if (checkBox.isSelected()) {
            if (!filters.getRegionsSelected().contains((String) toAdd)) {
                filters.addToRegions((String) toAdd);
            }
        } else {
            filters.removeFromRegions((String) toAdd);
        }
    }

    /**
     * Takes a holiday checkbox and checks if it has been selected.
     * Either adds or removes this holiday from the FilterManager's list of selections.
     *
     * @param checkBox Checkbox representing the given holiday.
     */
    public static void addToHoliday(CheckBox checkBox) {
        Object toAdd = checkBox.getUserData();
        int holiday = Integer.parseInt((String) toAdd);
        if (checkBox.isSelected()) {
            if (!filters.getHolidaysSelected().contains(holiday)) {
                filters.addToHolidays(holiday);
            }
        } else {
            filters.removeFromHolidays(holiday);
        }
    }
}
