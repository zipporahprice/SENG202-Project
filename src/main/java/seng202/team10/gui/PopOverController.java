package seng202.team10.gui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

public class PopOverController {

    private PopOver popOver;

    /**
     * Displays a notification message near the specified button when it is pressed.
     *
     * @param btn     The button for which the notification is displayed.
     * @param message The message to be displayed in the notification.
     */

    public void showNotificationOnButtonPress(Button btn, String message) {
        if (popOver != null && popOver.isShowing()) {
            popOver.hide();
        }
        Label label = new Label(message);
        label.setFont(new Font(20.0));
        label.setPadding(new Insets(5));
        popOver = new PopOver(label);
        popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
        popOver.show(btn);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5),
                popOver.getSkin().getNode());

        fadeOut.setDelay(Duration.millis(1500));
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> popOver.hide());
        fadeOut.play();
    }

    /**
     * Displays a popover near a TextField with a specified message and fade-out duration.
     *
     * @param message   The message to be displayed in the popover.
     * @param textField The TextField near which the popover should be displayed.
     * @param time      The duration (in seconds) for the fade-out animation.
     */
    public void showPopOver(String message, ComboBox<String> textField, double time) {
        Label label = new Label(message);
        popOver = new PopOver(label);
        popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
        popOver.show(textField);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(time),
                popOver.getSkin().getNode());

        fadeOut.setDelay(Duration.millis(1500));
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> popOver.hide());
        fadeOut.play();

    }

}
