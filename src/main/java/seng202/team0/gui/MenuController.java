package seng202.team0.gui;


/**
 * The interface defines methods for updating and loading data in a menu controller.
 * Classes that implement this interface must provide concrete implementations for these methods.
 */
public interface MenuController {

    /**
     * Update the data manager associated with the menu.
     */
    void updateManager();

    /**
     * Load initial data and settings into the menu manager.
     */
    void loadManager();
}
