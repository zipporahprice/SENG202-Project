package seng202.team0.gui;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class MapController {

    @FXML
    private WebView webView;

    private Stage stage;
    private WebEngine webEngine;
    private seng202.JavaScriptBridge javaScriptBridge;
    JSObject javaScriptConnector;



    public void init(Stage stage) {
        this.stage = stage;
        javaScriptBridge = new seng202.JavaScriptBridge();
        initMap();
        stage.sizeToScene();
    }

    /**
     * Initialises the WebView loading in the appropriate html and initialising important communicator
     * objects between Java and Javascript
     */
    private void initMap() {
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(getClass().getClassLoader().getResource("html/map.html").toExternalForm());
        // Forwards console.log() output from any javascript to info log
        WebConsoleListener.setDefaultListener((view, message, lineNumber, sourceId) ->
                System.out.println(String.format("Map WebView console log line: %d, message : %s", lineNumber, message)));

        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    // if javascript loads successfully
                    if (newState == Worker.State.SUCCEEDED) {
                        // set our bridge object
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaScriptBridge", javaScriptBridge);
                        // get a reference to the js object that has a reference to the js methods we need to use in java
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
                        // call the javascript function to initialise the map
                        javaScriptConnector.call("initMap");
                    }
                });
    }
}
