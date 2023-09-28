package seng202.team0.gui;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class TransitionHelper {

    /**
     * Toggles the visibility of a given node with a fade animation.
     *
     * @param node The node to toggle.
     */
    public static void toggleNodeWithFade(Node node) {
        FadeTransition transition = new FadeTransition(Duration.millis(500), node);

        if (node.isVisible()) {
            transition.setOnFinished(event -> node.setVisible(false)); // Set the action to hide the pane after fade-out
            transition.setFromValue(1.0); // Start from fully visible
            transition.setToValue(0.0);   // Transition to fully transparent (invisible)
        } else {
            node.setVisible(true); // Make the pane visible before starting fade-in
            transition.setOnFinished(null); // Reset the onFinished handler
            transition.setFromValue(0.0);   // Start from fully transparent (invisible)
            transition.setToValue(1.0);     // Transition to fully visible
        }

        transition.play();
    }

}
