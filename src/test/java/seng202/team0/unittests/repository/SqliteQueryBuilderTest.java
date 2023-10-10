package seng202.team0.unittests.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team0.repository.DatabaseManager;
import seng202.team0.repository.SqliteQueryBuilder;

import java.util.List;

/**
 * Test class for SQLiteQueryBuilder class
 *
 * @author Neil Alombro
 *
 */

public class SqliteQueryBuilderTest {

    /**
     * Tests create function.
     */
    @Test
    void testCreate() {
        SqliteQueryBuilder builder = SqliteQueryBuilder.create();
        Assertions.assertTrue(builder instanceof SqliteQueryBuilder);
    }

    /**
     * Tests select function.
     */
    @Test
    void testSelect() {
        SqliteQueryBuilder builder = SqliteQueryBuilder.create().select("id, ltd, lng");
        Assertions.assertEquals("SELECT id, ltd, lng ", builder.getQuery());
    }

    /**
     * Tests from function.
     */
    @Test
    void testFrom() {
        SqliteQueryBuilder builder = SqliteQueryBuilder.create().from("crashes");
        Assertions.assertEquals("FROM crashes ", builder.getQuery());
    }

    /**
     * Tests where function.
     */
    @Test
    void testWhere() {
        SqliteQueryBuilder builder = SqliteQueryBuilder.create().where("age > 30, speed > 30");
        Assertions.assertEquals("WHERE age > 30, speed > 30 ", builder.getQuery());
    }

    /**
     * Tests build function.
     */
    @Test
    void testBuild() {
        // Reset to make sure nothing in database
        DatabaseManager.getInstance().resetDb();

        List crashes = SqliteQueryBuilder.create()
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
        String query = SqliteQueryBuilder.create().getQuery();
        Assertions.assertEquals(query, "");
    }

}
