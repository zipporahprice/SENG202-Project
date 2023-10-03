package seng202.team0.unittests.repository;

import io.cucumber.java.hu.Ha;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.business.FilterManager;
import seng202.team0.io.CrashCSVImporter;
import seng202.team0.models.Crash;
import seng202.team0.models.Location;
import seng202.team0.repository.CrashDAO;
import seng202.team0.repository.DatabaseManager;
import seng202.team0.repository.SQLiteQueryBuilder;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for SQLiteQueryBuilder class
 *
 * @author Neil Alombro
 *
 */

public class SQLiteQueryBuilderTest {

    private SQLiteQueryBuilder builder;

    /**
     * Tests create function with set up for each test.
     */
    @BeforeEach
    void setUp() {
        builder = SQLiteQueryBuilder.create();
    }

    /**
     * Tests with function.
     */
    @Test
    void testWith() {
        // LinkedHashMap ensures that the order is by insertion. Other Map implementations
        // should work with the with function.
        Map<String, Number> map = new LinkedHashMap<>();
        map.put("start_lat", 53.15);
        map.put("start_long", 52.15);
        map.put("end_lat", 83.68);
        map.put("end_long", 94.92);

        String expectedQuery = "WITH constants AS (SELECT 53.15 AS start_lat, 52.15 AS start_long, 83.68 AS end_lat, 94.92 AS end_long) ";
        String query = builder.with("constants", map).getQuery();
        Assertions.assertEquals(expectedQuery, query);
    }


    /**
     * Tests select function.
     */
    @Test
    void testSelect() {
        String expectedQuery = "SELECT id, ltd, lng ";
        String query = builder.select("id, ltd, lng").getQuery();
        Assertions.assertEquals("SELECT id, ltd, lng ", query);
    }

    /**
     * Tests from function.
     */
    @Test
    void testFrom() {
        String expectedQuery = "FROM crashes ";
        String query = builder.from("crashes").getQuery();
        Assertions.assertEquals(expectedQuery, query);
    }

    /**
     * Tests where function.
     */
    @Test
    void testWhere() {
        String expectedQuery = "WHERE age > 30, speed > 30 ";
        String query = builder.where("age > 30, speed > 30").getQuery();
        Assertions.assertEquals(expectedQuery, query);
    }

    /**
     * Tests build function.
     */
    @Test
    void testBuild() {
        // Reset to make sure nothing in database
        DatabaseManager.getInstance().resetDB();

        List crashes = SQLiteQueryBuilder.create()
                                        .select("object_id, longitude, latitude")
                                        .from("crashes")
                                        .where("")
                                        .build();

        Assertions.assertTrue(crashes.size() == 0);
    }

    /**
     * Tests getQuery function.
     */
    @Test
    void testGetQuery() {
        String query = builder.getQuery();
        Assertions.assertEquals(query, "");
    }
}
