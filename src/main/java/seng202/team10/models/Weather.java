package seng202.team10.models;

/**
 * Weather enum represents different weather conditions associated with a crash object.
 * It provides a mapping between string representations of weather conditions and enum constants.
 *
 * @author Zipporah Price
 */
public enum Weather {

    FINE("Fine"),
    LIGHTRAIN("Light Rain"),
    HEAVYRAIN("Heavy Rain"),
    MISTORFOG("Mist or Fog"),
    SNOW("Snow"),
    NULL("Null");

    private final String name;

    Weather(String name) {
        this.name = name;
    }

    /**
     * Converts a string representation of weather into the corresponding Weather enum constant.
     *
     * @param stringWeather The string representation of the weather.
     * @return The Weather enum constant, or null if not found.
     */
    public static Weather stringToWeather(String stringWeather) {
        switch (stringWeather) {
            case "Fine": return Weather.FINE;
            case "Light rain": return Weather.LIGHTRAIN;
            case "Heavy rain": return Weather.HEAVYRAIN;
            case "Mist or Fog": return Weather.MISTORFOG;
            case "Snow": return Weather.SNOW;
            default: return Weather.NULL;
        }
    }

    /**
     * Gets the name of the weather.
     *
     * @return The name of the Weather as a String.
     */
    public String getName() {
        return name;
    }
}
