package seng202.team0.unittests.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.models.Favourite;
import seng202.team0.repository.DatabaseManager;
import seng202.team0.repository.SqliteQueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for SQLiteQueryBuilder class
 *
 * @author Neil Alombro
 *
 */

public class SqliteQueryBuilderTest {

    private SqliteQueryBuilder builder;

    /**
     * Tests create function.
     */
    @BeforeEach
    void testCreate() {
        builder = SqliteQueryBuilder.create();
    }

    /**
     * Tests insert function
     */
    @Test
    void testInsert() {
        builder.insert("crashes");
        String expectedQuery = "INSERT INTO crashes (speed_limit, crash_year, "
                + "crash_location1, crash_location2, severity, region, weather, "
                + "longitude, latitude, bicycle_involved, bus_involved, "
                + "car_involved, holiday, moped_involved, motorcycle_involved, "
                + "parked_vehicle_involved, pedestrian_involved, "
                + "school_bus_involved, train_involved, truck_involved) "
                + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        Assertions.assertEquals(expectedQuery, builder.getQuery());
    }

    /**
     * Tests select function.
     */
    @Test
    void testSelect() {
        builder.select("id, ltd, lng");
        String expectedQuery = "SELECT id, ltd, lng ";
        Assertions.assertEquals(expectedQuery, builder.getQuery());
    }

    /**
     * Tests from function.
     */
    @Test
    void testFrom() {
        builder.from("crashes");
        String expectedQuery = "FROM crashes ";
        Assertions.assertEquals(expectedQuery, builder.getQuery());
    }

    /**
     * Tests where function.
     */
    @Test
    void testWhere() {
        builder.where("age > 30, speed > 30");
        String expectedQuery = "WHERE age > 30, speed > 30 ";
        Assertions.assertEquals(expectedQuery, builder.getQuery());
    }

    /**
     * Test buildSetter function.
     */
    @Test
    void testBuildSetter() {
        // Reset to make sure nothing in database
        DatabaseManager.getInstance().resetDb();

        Favourite favourite = new Favourite("40 Little Oaks Drive", "University of Canterbury",
                143.657, 34.534,141.657, 33.534, "");
        List<Favourite> favourites = new ArrayList<>();
        favourites.add(favourite);

        builder.insert("favourites").buildSetter(favourites);
        Assertions.assertTrue(SqliteQueryBuilder.create().select("*").from("favourites").buildGetter().size() > 0);
    }


    /**
     * Tests buildGetter function.
     */
    @Test
    void testBuildGetter() {
        // Reset to make sure nothing in database
        DatabaseManager.getInstance().resetDb();

        List<?> crashes = builder.select("object_id, longitude, latitude").from("crashes").buildGetter();

        Assertions.assertEquals(0, crashes.size());
    }

    /**
     * Tests getQuery function.
     */
    @Test
    void testGetQuery() {
        String query = SqliteQueryBuilder.create().getQuery();
        Assertions.assertEquals(query, "");
    }

}
