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

        String expectedQuery = "WITH constants(start_lat, start_long, end_lat, end_long) AS (VALUES (53.15, 52.15, 83.68, 94.92))";
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

    @Test
    void testSomething() {
        Location startLocation = new Location(10.0, 20.0);
        Location endLocation = new Location(30.0, 40.0);

        double start_long_rad = Math.toRadians(startLocation.longitude);
        double start_lat_rad = Math.toRadians(startLocation.latitude);
        double end_long_rad = Math.toRadians(endLocation.longitude);
        double end_lat_rad = Math.toRadians(endLocation.latitude);

        double start_x = Math.cos(start_lat_rad) * Math.cos(start_long_rad);
        double start_y = Math.cos(start_lat_rad) * Math.sin(start_long_rad);
        double start_z = Math.sin(start_lat_rad);

        double end_x = Math.cos(end_lat_rad) * Math.cos(end_long_rad);
        double end_y = Math.cos(end_lat_rad) * Math.sin(end_long_rad);
        double end_z = Math.sin(end_lat_rad);

        Map<String, Number> constantsTable = new LinkedHashMap<>();
        constantsTable.put("start_x", start_x);
        constantsTable.put("start_y", start_y);
        constantsTable.put("start_z", start_z);
        constantsTable.put("end_x", end_x);
        constantsTable.put("end_y", end_y);
        constantsTable.put("end_z", end_z);

        double minLon = Math.min(startLocation.longitude, endLocation.longitude);
        double maxLon = Math.max(startLocation.longitude, endLocation.longitude);
        double minLat = Math.min(startLocation.latitude, endLocation.latitude);
        double maxLat = Math.max(startLocation.latitude, endLocation.latitude);

        // One kilometre in degrees
        double oneKilometreInDegrees = 0.008;

        String tableName = "locations";
        String crossProductMagnitude = "SQRT(POWER((COS(RADIANS(latitude)) * SIN(RADIANS(longitude)) - "
                + tableName + ".start_y) * (" + tableName + ".end_z - " + tableName + ".start_z) - (SIN(RADIANS(latitude)) - "
                + tableName + ".start_z) * (" + tableName + ".end_y - " + tableName + ".start_y), 2) + POWER((SIN(RADIANS(latitude)) - "
                + tableName + ".start_z) * (" + tableName + ".end_x - " + tableName + ".start_x) - (COS(RADIANS(latitude)) * COS(RADIANS(longitude)) - "
                + tableName + ".start_x) * (" + tableName + ".end_z - " + tableName + ".start_z), 2) + POWER((COS(RADIANS(latitude)) * COS(RADIANS(longitude)) - "
                + tableName + ".start_x) * (" + tableName + ".end_y - " + tableName + ".start_y) - (COS(RADIANS(latitude)) * SIN(RADIANS(longitude)) - "
                + tableName + ".start_y) * (" + tableName + ".end_x - " + tableName + ".start_x), 2))";
        String lineMagnitude = "SQRT(POWER(" + tableName + ".end_x - " + tableName + ".start_x, 2) + POWER("
                + tableName + ".end_y - " + tableName + ".start_y, 2) + POWER("
                + tableName + ".end_z - " + tableName + ".start_z, 2))";
        String aSinTheta = "ASIN(" + crossProductMagnitude + "/" + lineMagnitude + ")";
        String worldDistance = aSinTheta + " * 6371.0";

        FilterManager filterManager = FilterManager.getInstance();
        Location previousMin = filterManager.getViewPortMin();
        Location previousMax = filterManager.getViewPortMax();

        filterManager.setViewPortMin(new Location(minLat - oneKilometreInDegrees, minLon - oneKilometreInDegrees));
        filterManager.setViewPortMax(new Location(maxLat + oneKilometreInDegrees, maxLon + oneKilometreInDegrees));

        String filterWhere = filterManager.toString();

        filterManager.setViewPortMin(previousMin);
        filterManager.setViewPortMax(previousMax);

        String select = "AVG(severity)";
        String from = "crashes";
        String where = filterWhere + " AND " + worldDistance;

        String query = SQLiteQueryBuilder.create()
                .with(tableName, constantsTable)
                .select(select)
                .from(from)
                .where(where)
                .getQuery();

        System.out.println(query);

        Assertions.assertEquals(0,0);
    }
}
