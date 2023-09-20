package seng202.team0.unittests.models;

import org.junit.jupiter.api.*;
import seng202.team0.models.CrashSeverity;

import static seng202.team0.models.CrashSeverity.FATAL;

public class CrashSeverityTest {

    @Test
    void testStringToCrashSeverity() {
        CrashSeverity severity = CrashSeverity.stringToCrashSeverity("Fatal Crash");
        Assertions.assertEquals(FATAL, severity);
    }

    @Test
    void testValue() {
        CrashSeverity severity = CrashSeverity.stringToCrashSeverity("Minor Crash");
        int value = severity.getValue();
        Assertions.assertEquals(2, value);
    }

}
