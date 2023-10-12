package seng202.team0.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.tuple.Pair;
import seng202.team0.business.FilterManager;

/**
 * The FilteringMenuController class is responsible for managing interactions with filter options.
 * It implements two interfaces, 'Initializable' and 'MenuController' to handle
 * initialization and updates on filter settings.
 *
 * @author Team10
 */
public class FilteringMenuController implements Initializable, MenuController {

    @FXML
    private AnchorPane severityPane;
    @FXML
    private AnchorPane transportModePane;
    @FXML
    private Slider startDateSlider;
    @FXML
    private Slider endDateSlider;
    @FXML
    private Label startYearLabel;
    @FXML
    private Label endYearLabel;
    @FXML
    private AnchorPane weatherPane;
    @FXML
    private AnchorPane regionsPane;
    @FXML
    private AnchorPane holidayPane;
    @FXML
    private Button applyFiltersButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadManager();
    }


    /**
     * Handles the change in the slider value and updates the user interface accordingly.
     * This method is called when the user interacts with a slider to select a value.
     * It rounds the slider value to an integer, updates the year label,
     * and sets the earliest year filter.
     */
    @FXML
    public void sliderValueChange() {
        int startSliderValue = (int) Math.round(startDateSlider.getValue());
        int endSliderValue = (int) Math.round(endDateSlider.getValue());

        // Update labels for user
        startYearLabel.setText(Integer.toString(startSliderValue));
        endYearLabel.setText(Integer.toString(endSliderValue));

        // Ensure that the start date is always less than or equal to the end date
        if (startSliderValue > endSliderValue) {
            startDateSlider.setValue(endSliderValue);
            startYearLabel.setText(Integer.toString(endSliderValue));
        }

        // Updates Filter Manager with the date range for crash query
        FilterManager filters = FilterManager.getInstance();
        filters.setEarliestYear(startSliderValue);
        filters.setLatestYear(endSliderValue);
        // TODO here is where we can set class variables as the year instead of updating the manager every time

        clickableApplyFiltersButton();
    }

    /**
     * With the corresponding anchor pane, this onAction event function selects all filters.
     *
     * @param event ActionEvent object to get the corresponding AnchorPane
     */
    public void selectAllFilters(ActionEvent event) {
        Button selectAllButton = (Button) event.getSource();
        AnchorPane parent = (AnchorPane) selectAllButton.getParent();
        setCheckBoxesToState(parent, true);
        clickableApplyFiltersButton();
    }

    /**
     * With the corresponding anchor pane, this onAction event function deselects all filters.
     *
     * @param event ActionEvent object to get the corresponding AnchorPane
     */
    public void clearAllFilters(ActionEvent event) {
        Button selectAllButton = (Button) event.getSource();
        AnchorPane parent = (AnchorPane) selectAllButton.getParent();
        setCheckBoxesToState(parent, false);
        clickableApplyFiltersButton();
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
        clickableApplyFiltersButton();
    }

    /**
     * Updates the state of an "All" CheckBox based on the selection state of related CheckBoxes.
     * If all related CheckBoxes are selected, the "All" CheckBox is also selected;
     * otherwise, it is deselected.
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
     * Adds/removes a filter based on the state of a CheckBox and its associated parent AnchorPane.
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
     *
     * @param parent AnchorPane object to search through
     * @return Pair with types CheckBox and List of CheckBox objects
     */
    private List<CheckBox> getCheckBoxList(AnchorPane parent) {
        List<CheckBox> checkBoxes = new ArrayList<>();

        for (Object child : parent.getChildren()) {
            if (child instanceof VBox vertBox) {
                for (Object childCheckBox : vertBox.getChildren()) {
                    if (childCheckBox instanceof CheckBox checkBox) {
                        checkBoxes.add(checkBox);
                    }
                }
            }
        }

        return checkBoxes;
    }

    /**
     * Takes an AnchorPane and searches through for the CheckBox objects
     * to set as the same state as given in state parameter.
     *
     * @param parent AnchorPane object to search through
     * @param state Boolean value to set each CheckBox to
     */
    private void setCheckBoxesToState(AnchorPane parent, Boolean state) {
        for (Object child : parent.getChildren()) {
            if (child instanceof VBox vertBox) {
                for (Object childCheckBox : vertBox.getChildren()) {
                    if (childCheckBox instanceof  CheckBox checkBox) {
                        checkBox.setSelected(state);
                        addToFilters(checkBox, parent);
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
        List<CheckBox> checkBoxes = getCheckBoxList(parent);
        List<String> filterListStrings = filterList.stream()
                .map(Object::toString)
                .toList();

        // Updates checkboxes according to the list of filter values
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setSelected(filterListStrings.contains((String) checkBox.getUserData()));
        }
    }


    /**
     * Takes a Favourite object and updates.
     * the FilterManager singleton class and the checkboxes on the GUI,
     */
    @Override
    public void loadManager() {
        FilterManager filters = FilterManager.getInstance();

        // Retrieve all updated filter data
        List<Integer> severitiesSelected = filters.getSeveritiesSelected();
        List<String> modesSelected = filters.getModesSelected();
        final int earliestYear = filters.getEarliestYear();
        final int latestYear = filters.getLatestYear();
        final List<String> weathersSelected = filters.getWeathersSelected();
        final List<String> regionsSelected = filters.getRegionsSelected();
        final List<Integer> holidaysSelected = filters.getHolidaysSelected();

        // Updating checkboxes according to filters
        updateCheckboxesWithFilterList(severityPane, severitiesSelected);
        updateCheckboxesWithFilterList(transportModePane, modesSelected);
        startDateSlider.setValue(earliestYear);
        startYearLabel.setText(Integer.toString(earliestYear));
        endDateSlider.setValue(latestYear);
        endYearLabel.setText(Integer.toString(latestYear));
        updateCheckboxesWithFilterList(weatherPane, weathersSelected);
        updateCheckboxesWithFilterList(regionsPane, regionsSelected);
        updateCheckboxesWithFilterList(holidayPane, holidaysSelected);
    }

    @Override
    public void updateManager() {
        // TODO store the data in the manager
    }

    /**
     * OnAction event callback for "Apply Filters" button.
     */
    public void updateDataWithFilters() {
        MainController.javaScriptConnector.call("updateDataShown");
        notClickableApplyFiltersButton();
    }

    /**
     * Makes the "Apply Filters" button clickable by updating its style and enabling it.
     * This method removes the "inactive-button" style class, adds the
     * "address-button-add-style" style class,
     * and sets the button to be enabled, allowing user interaction.
     */
    public void clickableApplyFiltersButton() {
        applyFiltersButton.getStyleClass().remove("inactive-button");
        applyFiltersButton.getStyleClass().add("address-button-add-style");
        applyFiltersButton.setDisable(false);
    }

    /**
     * Makes the "Apply Filters" button not clickable by updating its style and disabling it.
     * This method removes the "address-button-add-style" style class,
     * adds the "inactive-button" style class,
     * and sets the button to be disabled, preventing user interaction.
     */
    public void notClickableApplyFiltersButton() {
        applyFiltersButton.getStyleClass().remove("address-button-add-style");
        applyFiltersButton.getStyleClass().add("inactive-button");
        applyFiltersButton.setDisable(true);
    }
}
