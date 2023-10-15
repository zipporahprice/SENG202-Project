package seng202.team10.business;

/**
 * Singleton class for storing settings options from the FXML controller class.
 *
 * @author Zipporah Price
 */
public class SettingsManager {

    private static SettingsManager settings;
    private String currentView;

    /**
     * Initializer of the SettingsManager class that sets default value "Automatic"
     * for String currentView.
     */
    private SettingsManager() {
        currentView = "None";
    }

    /**
     * Retrieves the singleton SettingsManager instance.
     *
     * @return The singleton instance of SettingsManager.
     */
    public static SettingsManager getInstance() {
        if (settings == null) {
            settings = new SettingsManager();
        }
        return settings;
    }

    public String getCurrentView() {
        return currentView;
    }

    public void setCurrentView(String view) {
        currentView = view;
    }
}
