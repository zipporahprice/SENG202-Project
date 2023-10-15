package seng202.team10.unittests.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team10.business.CrashManager;
import seng202.team10.repository.DatabaseManager;
import seng202.team10.repository.SqliteQueryBuilder;

/**
 * Testing MainController class.
 */

public class MainControllerTest {

    private DatabaseManager databaseManager;

    /**
     * Setup with resetting database at each test.
     */
    @BeforeEach
    @AfterEach
    void setUp() {
        databaseManager = DatabaseManager.getInstance();
        databaseManager.resetDb();
    }

    /**
     * Test for severitiesSelected method.
     */
    @Test
    void testingSeveritiesSelected() {
        // Imitating checking all the boxes
        List<Integer> severitiesSelected = new ArrayList<Integer>();
        severitiesSelected.add(1);
        severitiesSelected.add(2);
        severitiesSelected.add(4);
        severitiesSelected.add(8);

        List crashes = SqliteQueryBuilder
                .create()
                .select("object_id")
                .from("crashes")
                .where("severity IN (" + severitiesSelected.stream()
                        .map(Object::toString).collect(Collectors.joining(", ")) + ")")
                .buildGetter();

        List expectedCrashes = null;

        CrashManager manager = new CrashManager();
        expectedCrashes = manager.getCrashLocations();

        assert expectedCrashes != null;
        Assertions.assertEquals(crashes.size(), expectedCrashes.size());
    }

    /**
     * Test for weatherSelected method.
     */
    @Test
    void testingWeatherSelected() {
        // Like ticking all the checkboxes in weather
        List<String> weatherSelected = new ArrayList<>();
        weatherSelected.add("Fine");
        weatherSelected.add("Light Rain");
        weatherSelected.add("Heavy Rain");
        weatherSelected.add("Mist or Fog");
        weatherSelected.add("Snow");
        weatherSelected.add("Null");

        List<?> crashes = SqliteQueryBuilder
                .create()
                .select("object_id")
                .from("crashes")
                .where("weather IN (" + weatherSelected.stream().map(s -> "'" + s + "'")
                        .collect(Collectors.joining(", ")) + ")")
                .buildGetter();

        CrashManager manager = new CrashManager();
        List expectedCrashes = manager.getCrashLocations();

        Assertions.assertNotNull(expectedCrashes);
        Assertions.assertEquals(crashes.size(), expectedCrashes.size());
    }

    /**
     * Tear down with resetting the database.
     */
    @AfterEach
    void tearDown() {
        databaseManager.resetDb();
    }

}
