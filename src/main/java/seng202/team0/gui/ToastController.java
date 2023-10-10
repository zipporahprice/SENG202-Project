package seng202.team0.gui;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ToastController {

    @FXML
    private Label messageLabel;
    @FXML
    private StackPane rootPane;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void showMessage(String message, int millis) {
        messageLabel.setText(message);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), rootPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), rootPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);

        fadeIn.play();
        fadeIn.setOnFinished((e) -> {
            new Thread(() -> {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException ex) {
                }
                fadeOut.play();
            }).start();
        });

        fadeOut.setOnFinished((e) -> stage.close());
    }
}
