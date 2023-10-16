package seng202.team10.gui;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import seng202.team10.business.JavaScriptBridge;
import seng202.team10.models.GeoLocator;


/**
 * The controller class for managing the Map view in the application.
 * This class handles interactions between the JavaFX UI components and the WebView
 * used to display a map with JavaScript functionality.
 *
 * @author Team 10
 */

public class MapController {

    @FXML
    private WebView webView;

    private Stage stage;

    private GeoLocator geolocator;
    private WebEngine webEngine;
    private JavaScriptBridge javaScriptBridge;

    JSObject javaScriptConnector;

    /**
     * Sets the WebView component to be used for displaying web content.
     * This method assigns the provided WebView to the class field 'webView'.
     *
     * @param webView The WebView component to be set.
     */
    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    /**
     * Initializes the application's main stage and components.
     * This method sets up the primary stage, initializes the JavaScript bridge,
     * and configures the WebView for the map display. It also binds the WebView's
     * dimensions to a fraction of the stage's size and ensures the stage is sized
     * according to the scene.
     *
     * @param stage The primary stage of the JavaFX application.
     * @throws NullPointerException If the 'stage' object is not properly initialized
     *                              before calling this method.
     */
    public void init(Stage stage) {
        this.stage = stage;
        javaScriptBridge = new JavaScriptBridge();
        geolocator = new GeoLocator();
        initMap();
        stage.sizeToScene();

    }


    public JavaScriptBridge getJavaScriptBridge() {
        return javaScriptBridge;
    }

    /**
     * Initializes the WebView to display a map using JavaScript.
     * This method sets up the WebView to load an HTML file containing a map and
     * enables JavaScript support. It also establishes a bridge between Java and
     * JavaScript to communicate between the WebView and the Java application.
     * Console log output from JavaScript is forwarded to the info log.
     * When the HTML and JavaScript are fully loaded, the JavaScript function
     * 'initMap' is called to initialize the map.
     *
     * @throws NullPointerException If the 'webView' or 'javaScriptBridge' objects
     *                              are not properly initialized before calling this method.
     */
    public void initMap() {
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(getClass().getClassLoader().getResource("html/map.html").toExternalForm());

        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    // if javascript loads successfully
                    if (newState == Worker.State.SUCCEEDED) {
                        // set our bridge object
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaScriptBridge", javaScriptBridge);
                        // get a reference to the js object that has a reference
                        // to the js methods we need to use in java
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
                        // call the javascript function to initialise the map
                        javaScriptConnector.call("initMap");
                    }
                });
    }

}


