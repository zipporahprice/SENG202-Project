package seng202.team0.business;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for storing filters from the FXML controller classes.
 * Stores filters in lists of the categories.
 *
 * @author Neil Alombro
 */

public class FilterManager {
    private static FilterManager filters;
    private List<Integer> severitiesSelected;

    private FilterManager() {
        severitiesSelected = new ArrayList<>();
    }

    public static FilterManager getInstance() {
        if (filters == null) {
            filters = new FilterManager();
        }
        return filters;
    }

    public List<Integer> getSeveritiesSelected() {
        return this.severitiesSelected;
    }

    public void addToSeverities(Integer severity) {
        severitiesSelected.add(severity);
    }

    public void removeFromSeverities(Integer severity) {
        severitiesSelected.remove((Object)severity);
    }
}
