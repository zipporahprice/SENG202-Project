let map, baseLayer, heatmapLayer, markerLayer;
var javaScriptBridge; // must be declared as var (will not be correctly assigned in java with let keyword)
let markers = [];
var routes = [];


/**
 * This object can be returned to our java code, where we can call the functions we define inside it
 */
let jsConnector = {
    addMarker: addMarker,
    displayRoute: displayRoute,
    removeRoute: removeRoute,
    initMap: initMap,
    setData: setData
};

/**
 * creates and initialises the map, also defines on click event that calls java code
 */
function initMap() {
    baseLayer= new L.TileLayer('https://tile.csse.canterbury.ac.nz/hot/{z}/{x}/{y}.png', { // UCs tilemap server
        attribution: '© OpenStreetMap contributors<br>Served by University of Canterbury'
    });

    // Setup map
    let mapOptions = {
        center: [-41.0, 172.0],
        zoom: 5.5,
        layers:[baseLayer]
    };
    map = new L.map('map', mapOptions);

    // Setup potential layers for views
    updateHeatmap();
    markerLayer = L.markerClusterGroup();

    // Initialise layers and setup callbacks
    updateDataShown();
    map.on('zoomend', updateDataShown);
    map.on('moveend', updateDataShown);
    window.addEventListener('resize', updateHeatmap);

    mapIsReady();
}

function updateHeatmap() {
    const heatmapShowing = map.hasLayer(heatmapLayer);

    const cfg = {
        // radius should be small ONLY if scaleRadius is true (or small radius is intended)
        // if scaleRadius is false it will be the constant radius used in pixels
        "radius": 0.1,
        "maxOpacity": .4,
        // scales the radius based on map zoom
        "scaleRadius": true,
        // if set to false the heatmap uses the global maximum for colorization
        // if activated: uses the data maximum within the current map boundaries
        //   (there will always be a red spot with useLocalExtremas true)
        "useLocalExtrema": false,
        // which field name in your data represents the latitude - default "lat"
        latField: 'lat',
        // which field name in your data represents the longitude - default "lng"
        lngField: 'lng',
        // which field name in your data represents the data value - default "value"
        valueField: 'count'
    };
    heatmapLayer = new HeatmapOverlay(cfg);

    if (heatmapShowing) {
        setData();
        map.addLayer(heatmapLayer);
    }
}

function updateDataShown() {
    setFilteringViewport();
    setData();
    updateView();
}

function mapIsReady() {
        javaScriptBridge.mapLoaded();
}

function adjustHeatmapRadiusBasedOnZoom() {
    let zoomLevel = map.getZoom();

    let newRadius;
    if (zoomLevel >= 17) {
        newRadius = 0.0002;  // For street-level detail
    } else if (zoomLevel >= 15 && zoomLevel < 17) {
        newRadius = 0.001; // For street-level detail
    } else if (zoomLevel >= 13) {
        newRadius = 0.005;  // For neighborhood-level detail
    }
    else if (zoomLevel >= 10) {
        newRadius = 0.01;  // For neighborhood-level detail
    }  else {
        newRadius = 0.1;  // For city-level detail
    }

    heatmapLayer.cfg.radius=newRadius;

}

function setFilteringViewport() {
    const bounds = map.getBounds();
    const minLatitude = bounds.getSouth();
    const minLongitude = bounds.getWest();
    const maxLatitude = bounds.getNorth();
    const maxLongitude = bounds.getEast();
    javaScriptBridge.setFilterManagerViewport(minLatitude, minLongitude, maxLatitude, maxLongitude);
}

function automaticViewChange() {
    var zoomLevel = map.getZoom();
    if (zoomLevel >= 12) {
        if (map.hasLayer(heatmapLayer)) {
            map.removeLayer(heatmapLayer);
        }
        if (!map.hasLayer(markerLayer)) {
            map.addLayer(markerLayer);
        }
    }
    else {
        if (map.hasLayer(markerLayer)) {
            map.removeLayer(markerLayer);
        }
        if (!map.hasLayer(heatmapLayer)) {
            map.addLayer(heatmapLayer);
        }
    }

}

/**
 * Updates the view according to the user selection
 * Three views available:
 * Automatic - Changes from heatmap to crash locations at "12" zoomed in and back
 * Heatmap - Shows heatmap at all zooms
 * Crash Locations - Shows crash locations in clusters at all zooms
 */
function updateView() {
    var currentView = javaScriptBridge.currentView();

    if (currentView === "Automatic") {
        automaticViewChange();
        map.on('zoomend', adjustHeatmapRadiusBasedOnZoom);
        map.on('zoomend', automaticViewChange);
    } else if (currentView === "Heatmap") {
        map.on('zoomend', adjustHeatmapRadiusBasedOnZoom);
        map.off('zoomend', automaticViewChange);

        if (map.hasLayer(markerLayer)) {
            map.removeLayer(markerLayer);
        }
        map.addLayer(heatmapLayer);
    } else if (currentView === "Crash Locations") {
        map.off('zoomend', adjustHeatmapRadiusBasedOnZoom);
        map.off('zoomend', automaticViewChange);
        if (map.hasLayer(heatmapLayer)) {
            map.removeLayer(heatmapLayer);
        }
        map.addLayer(markerLayer);
    } else {
        // Default with "None" showing
        map.off('zoomend', adjustHeatmapRadiusBasedOnZoom);
        map.off('zoomend', automaticViewChange);
        if (map.hasLayer(heatmapLayer)) {
            map.removeLayer(heatmapLayer);
        }
        if (map.hasLayer(markerLayer)) {
            map.removeLayer(markerLayer);
        }
    }
}


/**
 * Adds a marker to the map and stores it in the markers array for later use (e.g. removal)
 * @param title tooltip to display on hover
 * @param lat latitude to place marker at
 * @param lng longitude to place marker at
 */
function addMarker(title, lat, lng) {
    var m = new L.Marker([lat, lng])
    m.bindPopup(title).openPopup()
    m.addTo(map)
    markers.push(m)
}

/**
 * Displays a route with two or more waypoints for cars (e.g. roads and ferries) and displays it on the map
 * @param waypointsIn a string representation of an array of lat lng json objects [("lat": -42.0, "lng": 173.0), ...]
 */
function displayRoute(routesIn, transportMode) {
    removeRoute();

    var routesArray = JSON.parse(routesIn);
    console.log(routesArray);

    var mode = getMode(transportMode);

    routesArray.forEach(waypointsIn => {
        var waypoints = [];

        waypointsIn.forEach(element => waypoints.push(new L.latLng(element.lat, element.lng)));
        console.log(waypoints);
        var newRoute = L.Routing.control({
            waypoints: waypoints,
            routeWhileDragging: true,
            router: L.Routing.mapbox('pk.eyJ1IjoiemlwcG9yYWhwcmljZSIsImEiOiJjbG45cWI3OGYwOTh4MnFyMWsya3FpbjF2In0.RM37Ev9aUxEwKS5nMxpCpg', { profile: mode })
        }).addTo(map);
        routes.push(newRoute);
    });
}

function getMode(transportMode) {
    var mode;
    switch (transportMode) {
        case "car":
            mode = 'mapbox/driving';
            break;
        case "bike":
            mode = 'mapbox/cycling';
            break;
        case "walking":
            mode = 'mapbox/walking';
            break;
        default:
            mode = 'mapbox/driving';
            break;
    }
    return mode;
}

/**
 * Removes the current route being displayed (will not do anything if there is no route currently displayed)
 */
function removeRoute() {
    routes.forEach((r) => {
        r.remove();
    });
    routes = [];
}

function getSeverityStringFromValue(severity) {
    switch (severity) {
        case 1: return "Non-Injury";
        case 2: return "Minor Crash";
        case 4: return "Major Crash";
        case 8: return "Death";
        default: return "Invalid";
    }
}

function getMarkerIcon(severity) {
    var iconUrl;
    switch (severity) {
        case 1: // Non-Injury
            iconUrl = 'crash_markers/non_injury.png';
            break;
        case 2: // Minor Crash
            iconUrl = 'crash_markers/minor_crash.png';
            break;
        case 4: // Major Crash
            iconUrl = 'crash_markers/major_crash.png';
            break;
        case 8: // Death
            iconUrl = 'crash_markers/death_crash.png';
            break;
        default: // Default icon
            iconUrl = 'crash_markers/non_injury.png';
            break;
    }

    return L.icon({
        iconUrl: iconUrl,
        iconSize: [24, 32.7], // Adjust the icon size as needed
    });
}

function setData() {
    var crashesJSON = javaScriptBridge.crashes();
    var crashes = JSON.parse(crashesJSON);

    var testData = {
        max: 10,
        data: crashes
    };
    markerLayer.clearLayers();

    for (var i = 0; i < crashes.length; i++) {
        var a = crashes[i];
        var severity = getSeverityStringFromValue(a.severity);
        var markerIcon = getMarkerIcon(a.severity);
        var marker = L.marker(new L.LatLng(a.lat, a.lng), { title: severity, icon: markerIcon });
        marker.bindPopup("<div style='font-size: 16px;' class='popup-content'>" +
            "<p><strong>Latitude:</strong> " + a.lat + "</p>" +
            "<p><strong>Longitude:</strong> " + a.lng + "</p>" +
            "<p><strong>Severity:</strong> " + severity + "</p>" +
            "<p><strong>Year:</strong> " + a.crash_year + "</p>" + // Add year
            "<p><strong>Weather:</strong> " + a.weather + "</p>" + // Add weather
            "</div>"
        );
        markerLayer.addLayer(marker);
    }

    heatmapLayer.setData(testData);
}
