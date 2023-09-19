package seng202.team0.unittests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team0.business.FilterManager;

public class FilterManagerTest {

    @Test
    void testGetInstance() {
        FilterManager filters = FilterManager.getInstance();
        Assertions.assertTrue(filters instanceof FilterManager);
    }
}
