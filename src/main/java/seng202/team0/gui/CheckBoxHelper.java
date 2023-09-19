package seng202.team0.gui;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.lang3.tuple.Pair;
import seng202.team0.business.FilterManager;
import seng202.team0.models.Favourite;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Helper class for the MainController class that
 * has functions that are not directly called and bound
 * to an event with the GUI elements.
 *
 * @author Angelica Silva
 * @author Christopher Wareing
 * @author Neil Alombro
 * @author Todd Vermeir
 * @author William Thompson
 * @author Zipporah Price
 */

public class CheckBoxHelper {
    AnchorPane severityPane;
    AnchorPane transportModePane;
    Slider dateSlider;
    Label currentYearLabel;
    AnchorPane weatherPane;
    AnchorPane regionsPane;

    public CheckBoxHelper(AnchorPane severityPane, AnchorPane transportModePane, Slider dateSlider,
                          Label currentYearLabel, AnchorPane weatherPane, AnchorPane regionsPane) {
        this.severityPane = severityPane;
        this.transportModePane = transportModePane;
        this.dateSlider = dateSlider;
        this.currentYearLabel = currentYearLabel;
        this.weatherPane = weatherPane;
        this.regionsPane = regionsPane;
    }

    public void updateCheckboxesWithFavourites(Favourite favourite) {
        // Update FilterManager class with the filters associated to the favourite route
        FilterManager filters = FilterManager.getInstance();
        filters.updateFiltersWithQueryString(favourite.getFilters());

        // Retrieve all updated filter data
        List<Integer> severitiesSelected = filters.getSeveritiesSelected();
        List<String> modesSelected = filters.getModesSelected();
        int earliestYear = filters.getEarliestYear();
        List<String> weathersSelected = filters.getWeathersSelected();
        List<String> regionsSelected = filters.getRegionsSelected();

        // Updating checkboxes according to filters
        updateCheckboxesWithFilterList(severityPane, severitiesSelected);
        updateCheckboxesWithFilterList(transportModePane, modesSelected);
        dateSlider.setValue(earliestYear);
        currentYearLabel.setText(Integer.toString(earliestYear));
        updateCheckboxesWithFilterList(weatherPane, weathersSelected);
        updateCheckboxesWithFilterList(regionsPane, regionsSelected);
    }

    /**
     * Updates the state of an "All" CheckBox based on the selection state of a list of related CheckBoxes.
     * If all related CheckBoxes are selected, the "All" CheckBox is also selected; otherwise, it is deselected.
     *
     * @param allCheckBox The "All" CheckBox to update.
     * @param checkBoxes  The list of related CheckBoxes to check for selection.
     */
    public void updateAllCheckBox(CheckBox allCheckBox, List<CheckBox> checkBoxes) {
        boolean allSelected = true;
        for (CheckBox checkBox : checkBoxes) {
            if (!checkBox.isSelected()) {
                allSelected = false;
                break;
            }
        }
        allCheckBox.setSelected(allSelected);
    }

    public Pair<CheckBox, List<CheckBox>> getAllCheckBoxAndCheckBoxList(AnchorPane parent) {
        CheckBox allCheckBox = null;
        List<CheckBox> checkBoxes = new ArrayList<>();

        for (Object child : parent.getChildren()) {
            if (child instanceof VBox) {
                for (Object childCheckBox : ((VBox) child).getChildren()) {
                    if (childCheckBox instanceof  CheckBox) {
                        if (!Objects.equals(((CheckBox) childCheckBox).getText(), "All")) {
                            checkBoxes.add((CheckBox) childCheckBox);
                        } else {
                            allCheckBox = (CheckBox) childCheckBox;
                        }
                    }
                }
            }
        }

        return Pair.of(allCheckBox, checkBoxes);
    }

    /**
     * Takes in an AnchorPane to search through and updates the individual checkbox (including the
     * all checkbox) according to the included filters in the filterList.
     *
     * @param parent AnchorPane object corresponding to the list of filters
     * @param filterList List of values that could match with the userData variable of a CheckBox
     */
    private void updateCheckboxesWithFilterList(AnchorPane parent, List filterList) {
        // Runs helper function to get all checkbox and list of other checkboxes
        Pair<CheckBox, List<CheckBox>> result = getAllCheckBoxAndCheckBoxList(parent);
        CheckBox allCheckBox = result.getLeft();
        List<CheckBox> checkBoxes = result.getRight();

        // Updates checkboxes according to the list of filter values
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setSelected(filterList.contains(checkBox.getUserData()));
        }

        // Updates all checkbox to see if updated filters should have the box checked
        updateAllCheckBox(allCheckBox, checkBoxes);
    }
}
