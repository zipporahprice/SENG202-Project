package seng202.team10.unittests.business;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team10.business.CrashManager;
import seng202.team10.business.FilterManager;


/**
 * Test suite for validating the functionality of {@link CrashManager}.
 *
 * <p>Ensures the proper retrieval and handling of crash locations, utilizing
 * specific filter criteria through the {@link FilterManager} and asserting
 * their correctness in generating desired filter strings.
 *
 * @author Team 10
 */
public class CrashManagerTest {

    @Test
    void testGetCrashLocations() {
        FilterManager filters = FilterManager.getInstance();
        String filterString = "severity IN (1, 2) "
                + "AND (bicycle_involved = 1 OR moped_involved = 1) "
                + "AND crash_year BETWEEN 2000 AND 2023 "
                + "AND weather IN (\"Fine\", \"Light Rain\", \"Heavy Rain\") "
                + "AND region IN (\"Canterbury\") "
                + "AND holiday IN (0, 1)";
        filters.updateFiltersWithQueryString(filterString);
        Assertions.assertEquals(filterString, filters.toString());
    }

}
