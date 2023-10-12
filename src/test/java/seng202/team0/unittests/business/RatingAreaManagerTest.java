package seng202.team0.unittests.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team0.business.RatingAreaManager;

public class RatingAreaManagerTest {

    @Test
    void testGetInstance() {
        RatingAreaManager manager = RatingAreaManager.getInstance();
        Assertions.assertTrue(manager instanceof RatingAreaManager);
    }

}
