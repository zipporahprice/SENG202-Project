package seng202.team0.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.tuple.Pair;
import seng202.team0.business.FilterManager;
import seng202.team0.models.Favourite;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FilteringMenuController implements Initializable {

    @FXML
    private AnchorPane severityPane;
    @FXML
    private AnchorPane transportModePane;
    @FXML
    private Slider dateSlider;
    @FXML
    private Label currentYearLabel;
    @FXML
    private AnchorPane weatherPane;
    @FXML
    private AnchorPane regionsPane;
    @FXML
    private AnchorPane holidayPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateCheckboxesWithFilterManager();
    }


    /**
     * Handles the change in the slider value and updates the user interface accordingly.
     * This method is called when the user interacts with a slider to select a value.
     * It rounds the slider value to an integer, updates the year label, and sets the earliest year filter.
     */
    @FXML
    public void sliderValueChange() {
        int sliderValue = (int)Math.round(dateSlider.getValue());

        // Update year label for user
        currentYearLabel.setText(Integer.toString(sliderValue));

        // Updates Filter Manager with the earliest year for crash query
        FilterManager filters = FilterManager.getInstance();
        filters.setEarliestYear(sliderValue);
    }

    /**
     * Handles the event triggered when an "All" CheckBox is selected or deselected.
     * This method is typically used to select or deselect all other related CheckBoxes within the same group.
     *
     * @param event The ActionEvent triggered by the "All" CheckBox.
     */
    @FXML
    public void handleAllCheckBoxEvent(ActionEvent event) {
        // Initialise parent to search through and what to set
        CheckBox allCheckBox = (CheckBox) event.getSource();
        AnchorPane parent = (AnchorPane) allCheckBox.getParent().getParent();
        boolean allSelected = allCheckBox.isSelected();

        // Use helper function to set all checkboxes to the same state as all checkbox
        setCheckBoxesFromAllCheckBoxState(parent, allSelected);
    }

    /**
     * Handles the event triggered when a CheckBox is selected or deselected.
     * This method is responsible for updating filters and potentially the "All" CheckBox state
     * within the same group of CheckBoxes.
     *
     * @param event The ActionEvent triggered by the CheckBox.
     */
    @FXML
    public void handleCheckBoxEvent(ActionEvent event) {
        CheckBox checkBox = (CheckBox) event.getSource();
        AnchorPane parent = (AnchorPane) checkBox.getParent().getParent();

        addToFilters(checkBox, parent);
        // Runs helper function to get all checkbox and list of other checkboxes
        Pair<CheckBox, List<CheckBox>> result = getAllCheckBoxAndCheckBoxList(parent);
        CheckBox allCheckBox = result.getLeft();
        List<CheckBox> checkBoxes = result.getRight();

        assert allCheckBox != null;
        updateAllCheckBox(allCheckBox, checkBoxes);
    }

    /**
     * Updates the state of an "All" CheckBox based on the selection state of a list of related CheckBoxes.
     * If all related CheckBoxes are selected, the "All" CheckBox is also selected; otherwise, it is deselected.
     *
     * @param allCheckBox The "All" CheckBox to update.
     * @param checkBoxes  The list of related CheckBoxes to check for selection.
     */
    private void updateAllCheckBox(CheckBox allCheckBox, List<CheckBox> checkBoxes) {
        boolean allSelected = true;
        for (CheckBox checkBox : checkBoxes) {
            if (!checkBox.isSelected()) {
                allSelected = false;
                break;
            }
        }
        allCheckBox.setSelected(allSelected);
    }

    /**
     * Adds or removes a filter item based on the state of a CheckBox and its associated parent AnchorPane.
     * This method is used to manage and update filtering options based on user selections.
     *
     * @param checkBox The CheckBox representing the filter item.
     * @param parent   The parent AnchorPane of the CheckBox.
     */
    private void addToFilters(CheckBox checkBox, AnchorPane parent) {
        FilterManager filters = FilterManager.getInstance();
        Object toAdd = checkBox.getUserData();

        if (parent.equals(transportModePane)) {
            if (checkBox.isSelected()) {
                if (!filters.getModesSelected().contains((String) toAdd)) {
                    filters.addToModes((String) toAdd);
                }
            } else {
                filters.removeFromModes((String) toAdd);
            }
        } else if (parent.equals(weatherPane)) {
            if (checkBox.isSelected()) {
                if (!filters.getWeathersSelected().contains((String) toAdd)) {
                    filters.addToWeathers((String) toAdd);
                }
            } else {
                filters.removeFromWeathers((String) toAdd);
            }
        } else if (parent.equals(severityPane)) {
            int severity = Integer.parseInt((String) toAdd);
            if (checkBox.isSelected()) {
                if (!filters.getSeveritiesSelected().contains(severity)) {
                    filters.addToSeverities(severity);
                }
            } else {
                filters.removeFromSeverities(severity);
            }
        } else if (parent.equals(regionsPane)) {
            if (checkBox.isSelected()) {
                if (!filters.getRegionsSelected().contains((String) toAdd)) {
                    filters.addToRegions((String) toAdd);
                }
            } else {
                filters.removeFromRegions((String) toAdd);
            }
        } else if (parent.equals(holidayPane)) {
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

    /**
     * Takes an AnchorPane object and searches through for the allCheckBox and a list
     * of all other checkBoxes in the AnchorPane (reflective of all option checkboxes).
     * @param parent AnchorPane object to search through
     * @return Pair with types CheckBox and List of CheckBox corresponding to allCheckBox and checkBoxes
     */
    private Pair<CheckBox, List<CheckBox>> getAllCheckBoxAndCheckBoxList(AnchorPane parent) {
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
     * Takes an AnchorPane and searches through for the CheckBox objects
     * to set as the same state as given in allCheckBoxState.
     * @param parent AnchorPane object to search through
     * @param allCheckBoxState Boolean value to set each CheckBox to
     */
    private void setCheckBoxesFromAllCheckBoxState(AnchorPane parent, Boolean allCheckBoxState) {
        for (Object child : parent.getChildren()) {
            if (child instanceof VBox) {
                for (Object childCheckBox : ((VBox) child).getChildren()) {
                    if (childCheckBox instanceof  CheckBox) {
                        if (!Objects.equals(((CheckBox) childCheckBox).getText(), "All")) {
                            ((CheckBox) childCheckBox).setSelected(allCheckBoxState);
                            addToFilters((CheckBox) childCheckBox, parent);
                        }
                    }
                }
            }
        }
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
        List<String> filterListStrings = filterList.stream()
                .map(Object::toString)
                .toList();

        // Updates checkboxes according to the list of filter values
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setSelected(filterListStrings.contains((String)checkBox.getUserData()));
        }

        // Updates all checkbox to see if updated filters should have the box checked
        updateAllCheckBox(allCheckBox, checkBoxes);
    }


    /**
     * Takes a Favourite object and updates
     * the FilterManager singleton class and the checkboxes on the GUI,
     */
    public void updateCheckboxesWithFilterManager() {
        FilterManager filters = FilterManager.getInstance();

        // Retrieve all updated filter data
        List<Integer> severitiesSelected = filters.getSeveritiesSelected();
        List<String> modesSelected = filters.getModesSelected();
        int earliestYear = filters.getEarliestYear();
        List<String> weathersSelected = filters.getWeathersSelected();
        List<String> regionsSelected = filters.getRegionsSelected();
        List<Integer> holidaysSelected = filters.getHolidaysSelected();

        // Updating checkboxes according to filters
        updateCheckboxesWithFilterList(severityPane, severitiesSelected);
        updateCheckboxesWithFilterList(transportModePane, modesSelected);
        dateSlider.setValue(earliestYear);
        currentYearLabel.setText(Integer.toString(earliestYear));
        updateCheckboxesWithFilterList(weatherPane, weathersSelected);
        updateCheckboxesWithFilterList(regionsPane, regionsSelected);
        updateCheckboxesWithFilterList(holidayPane, holidaysSelected);
    }

}
