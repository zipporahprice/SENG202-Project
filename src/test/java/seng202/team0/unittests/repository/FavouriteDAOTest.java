package seng202.team0.unittests.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.models.Favourite;
import seng202.team0.repository.DatabaseManager;
import seng202.team0.repository.FavouriteDao;

import java.io.File;
import java.util.List;

/**
 * Test class for FavouriteDAO class
 *
 * @author Neil Alombro
 *
 */

public class FavouriteDAOTest {
    private static FavouriteDao testDAO;
    private static File testFile;
    private static Favourite testFavourite = new Favourite("Hello", "It's Me",
                                            17.25, 16.34, 13.45, 14.68, "filters");

    /**
     * Initialising DAO, reseting database, and adding one test.
     */
    @BeforeEach
    void testCreate() {
        testDAO = new FavouriteDao();
        DatabaseManager.getInstance().resetDb();

        // Add one so that get tests work
        testDAO.addOne(testFavourite);
    }

    /**
     * Test getAll function.
     */
    @Test
    void testGetAll() {
        int size = testDAO.getAll().size();
        Assertions.assertEquals(1, size);
    }

    /**
     * Test getOne function.
     */
    @Test
    void testGetOne() {
        Favourite favourite = testDAO.getOne(1);

        // TODO change to be meaningful instead of just seeing if filters is the same
        Assertions.assertEquals(testFavourite.getFilters(), favourite.getFilters());
    }

    /**
     * Test addOne function.
     */
    @Test
    void testAddOne() {
        List beforeFavourites = testDAO.getAll();
        testDAO.addOne(testFavourite);
        List afterFavourites = testDAO.getAll();

        Assertions.assertEquals(beforeFavourites.size() + 1, afterFavourites.size());
    }

    /**
     *  Tear down of variables to clear.
     */
    @AfterAll
    static void tearDown() {
        testDAO = null;
        testFile = null;
        testFavourite = null;
    }
}
