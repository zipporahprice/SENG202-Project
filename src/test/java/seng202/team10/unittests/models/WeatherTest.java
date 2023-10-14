package seng202.team10.unittests.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import seng202.team10.models.Weather;

/**
 * Testing Weather enum.
 */

public class WeatherTest {

    /**
     * Parameterizes testing for stringToWeather method.
     *
     * @param weatherString string to test
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "Fine", "Light rain", "Heavy rain",
        "Mist or Fog", "Snow", "Unknown Weather"
    })
    public void testStringToWeather(String weatherString) {
        Weather actualWeather = Weather.stringToWeather(weatherString);
        Weather expectedWeather = switch (weatherString) {
            case "Fine" -> Weather.FINE;
            case "Light rain" -> Weather.LIGHTRAIN;
            case "Heavy rain" -> Weather.HEAVYRAIN;
            case "Mist or Fog" -> Weather.MISTORFOG;
            case "Snow" -> Weather.SNOW;
            default -> Weather.NULL;
        };

        Assertions.assertEquals(expectedWeather, actualWeather);
    }

    /**
     * Testing getName method.
     */
    @Test
    void testGetName() {
        Weather weather = Weather.FINE;
        String weatherName = weather.getName();
        Assertions.assertEquals("Fine", weatherName);
    }

}
