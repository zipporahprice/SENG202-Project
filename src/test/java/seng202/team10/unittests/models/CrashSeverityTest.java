package seng202.team10.unittests.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import seng202.team10.models.CrashSeverity;

/**
 * Test class for the CrashSeverity enum.
 */

public class CrashSeverityTest {

    /**
     * Parameterised tests for stringToCrashSeverity method.
     *
     * @param stringSeverity severity string to test
     */
    @ParameterizedTest
    @ValueSource(strings = {"Non-Injury Crash", "Minor Crash",
                            "Serious Crash", "Fatal Crash", "None"})
    void testStringToCrashSeverity(String stringSeverity) {
        CrashSeverity result = CrashSeverity.stringToCrashSeverity(stringSeverity);
        switch (stringSeverity) {
            case "Non-Injury Crash" -> Assertions.assertEquals(CrashSeverity.NONINJURY, result);
            case "Minor Crash" -> Assertions.assertEquals(CrashSeverity.MINOR, result);
            case "Serious Crash" -> Assertions.assertEquals(CrashSeverity.SERIOUS, result);
            case "Fatal Crash" -> Assertions.assertEquals(CrashSeverity.FATAL, result);
            default -> Assertions.assertNull(result);
        }
    }

    /**
     * Testing getValue method.
     */
    @Test
    void testGetValue() {
        CrashSeverity severity = CrashSeverity.stringToCrashSeverity("Minor Crash");
        int value = severity.getValue();
        Assertions.assertEquals(4, value);
    }

    /**
     * Parameterized tests for intToString method.
     *
     * @param intSeverity severity int to test
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 4, 16, 64, 9})
    void testIntToString(int intSeverity) {
        String result = CrashSeverity.intToString(intSeverity);
        switch (intSeverity) {
            case 1 -> Assertions.assertEquals("Non-Injury Crash", result);
            case 4 -> Assertions.assertEquals("Minor Crash", result);
            case 16 -> Assertions.assertEquals("Serious Crash", result);
            case 64 -> Assertions.assertEquals("Fatal Crash", result);
            default -> Assertions.assertNull(result);
        }
    }

}
