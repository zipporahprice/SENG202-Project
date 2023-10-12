package seng202.team0.unittests.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team0.business.SettingsManager;

public class SettingsManagerTest {

    @Test
    void testGetInstance() {
        SettingsManager settingsManager = SettingsManager.getInstance();
        Assertions.assertTrue(settingsManager instanceof SettingsManager);
    }

}
