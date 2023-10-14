package seng202.team10.unittests.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team10.business.SettingsManager;

public class SettingsManagerTest {

    @Test
    void testGetInstance() {
        SettingsManager settingsManager = SettingsManager.getInstance();
        Assertions.assertNotNull(settingsManager);
    }

    @Test
    void testSingleton() {
        SettingsManager settingsManager1 = SettingsManager.getInstance();
        SettingsManager settingsManager2 = SettingsManager.getInstance();
        Assertions.assertEquals(settingsManager1, settingsManager2);
    }

}
