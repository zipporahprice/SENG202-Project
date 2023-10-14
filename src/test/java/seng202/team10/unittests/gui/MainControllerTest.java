package seng202.team10.unittests.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team10.business.CrashManager;
import seng202.team10.repository.DatabaseManager;
import seng202.team10.repository.SqliteQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainControllerTest {

    private DatabaseManager databaseManager;

    @BeforeEach
    void resetDatabase() {
        databaseManager = DatabaseManager.getInstance();
        databaseManager.resetDb();
    }

    // TODO make integration test for filtering
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
                .where("severity IN (" + severitiesSelected.stream().map(Object::toString).collect(Collectors.joining(", ")) + ")")
                .buildGetter();

        List expectedCrashes = null;

        CrashManager manager = new CrashManager();
        expectedCrashes = manager.getCrashLocations();

        assert expectedCrashes != null;
        Assertions.assertEquals(crashes.size(), expectedCrashes.size());
    }

    @Test
    void testingWeatherSelected() {
        //like ticking all the checkboxes in weather
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

}
