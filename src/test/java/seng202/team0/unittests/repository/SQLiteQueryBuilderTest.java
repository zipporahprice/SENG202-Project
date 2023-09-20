package seng202.team0.unittests.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team0.repository.SQLiteQueryBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Test class for SQLiteQueryBuilder class
 *
 * @author Neil Alombro
 *
 */

public class SQLiteQueryBuilderTest {

    /**
     * Tests create function.
     */
    @Test
    void testCreate() {
        SQLiteQueryBuilder builder = SQLiteQueryBuilder.create();
        Assertions.assertTrue(builder instanceof SQLiteQueryBuilder);
    }

    /**
     * Tests select function.
     */
    @Test
    void testSelect() {
        SQLiteQueryBuilder builder = SQLiteQueryBuilder.create().select("id, ltd, lng");
        Assertions.assertEquals("SELECT id, ltd, lng ", builder.getQuery());
    }

    /**
     * Tests from function.
     */
    @Test
    void testFrom() {
        SQLiteQueryBuilder builder = SQLiteQueryBuilder.create().from("crashes");
        Assertions.assertEquals("FROM crashes ", builder.getQuery());
    }

    /**
     * Tests where function.
     */
    @Test
    void testWhere() {
        SQLiteQueryBuilder builder = SQLiteQueryBuilder.create().where("age > 30, speed > 30");
        Assertions.assertEquals("WHERE age > 30, speed > 30 ", builder.getQuery());
    }

    /**
     * Tests build function.
     */
    @Test
    void testBuild() throws SQLException {
        List crashes = SQLiteQueryBuilder.create()
                                        .select("object_id, longitude, latitude")
                                        .from("crashes")
                                        .where("severity = 8")
                                        .build();

        // Note: Assumes there is one crash at a speed limit of 100
        Assertions.assertTrue(crashes.size() > 1);
    }

    /**
     * Tests getQuery function.
     */
    @Test
    void testGetQuery() {
        String query = SQLiteQueryBuilder.create().getQuery();
        Assertions.assertEquals(query, "");
    }

}
