package seng202.team0.unitttests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team0.repository.SQLiteQueryBuilder;

public class SQLiteQueryBuilderTest {

    @Test
    void testCreate() {
        SQLiteQueryBuilder builder = SQLiteQueryBuilder.create();
        Assertions.assertTrue(builder instanceof SQLiteQueryBuilder);
    }

    @Test
    void testSelect() {
        SQLiteQueryBuilder builder = SQLiteQueryBuilder.create().select("id, ltd, lng");
        Assertions.assertEquals("SELECT id, ltd, lng", builder.getQuery());
    }

    @Test
    void testFrom() {
        SQLiteQueryBuilder builder = SQLiteQueryBuilder.create().from("crashes");
        Assertions.assertEquals("FROM crashes", builder.getQuery());
    }

    @Test
    void testWhere() {
        SQLiteQueryBuilder builder = SQLiteQueryBuilder.create().where("age > 30, speed > 30");
        Assertions.assertEquals("WHERE age > 30, speed > 30", builder.getQuery());
    }


}
