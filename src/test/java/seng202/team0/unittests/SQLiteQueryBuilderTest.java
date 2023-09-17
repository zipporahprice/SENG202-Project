package seng202.team0.unittests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team0.business.CrashManager;
import seng202.team0.business.FilterManager;
import seng202.team0.models.Crash;
import seng202.team0.models.JavaScriptBridge;
import seng202.team0.repository.SQLiteQueryBuilder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class SQLiteQueryBuilderTest {

    @Test
    void testCreate() {
        SQLiteQueryBuilder builder = SQLiteQueryBuilder.create();
        Assertions.assertTrue(builder instanceof SQLiteQueryBuilder);
    }

    @Test
    void testSelect() {
        SQLiteQueryBuilder builder = SQLiteQueryBuilder.create().select("id, ltd, lng");
        Assertions.assertEquals("SELECT id, ltd, lng ", builder.getQuery());
    }

    @Test
    void testFrom() {
        SQLiteQueryBuilder builder = SQLiteQueryBuilder.create().from("crashes");
        Assertions.assertEquals("FROM crashes ", builder.getQuery());
    }

    @Test
    void testWhere() {
        SQLiteQueryBuilder builder = SQLiteQueryBuilder.create().where("age > 30, speed > 30");
        Assertions.assertEquals("WHERE age > 30, speed > 30 ", builder.getQuery());
    }

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

    @Test
    void testGetQuery() {
        String query = SQLiteQueryBuilder.create().getQuery();
        Assertions.assertEquals(query, "");
    }

}
