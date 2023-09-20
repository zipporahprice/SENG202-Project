package seng202.team0.gui;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import seng202.team0.models.Crash;

public class HelpPageController {

    private Stage stage;

    public void init(Stage stage) {
        this.stage = stage;
        stage.sizeToScene();
    }
}
