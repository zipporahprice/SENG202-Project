package seng202.team10.models;

/**
 * Enumeration representing different levels of crash severity.
 *
 * @author Angelica Silva
 * @author Christopher Wareing
 * @author Neil Alombro
 * @author Todd Vermeir
 * @author William Thompson
 * @author Zipporah Price
 */
public enum CrashSeverity {
    NONINJURY(1), MINOR(4), SERIOUS(16), FATAL(64);
    private final int value;

    CrashSeverity(int value) {
        this.value = value;
    }

    /**
     * Converts a string representation of crash severity to the corresponding enum value.
     *
     * @param stringSeverity The string representation of crash severity.
     * @return The corresponding CrashSeverity enum value, or null if no match is found.
     */
    public static CrashSeverity stringToCrashSeverity(String stringSeverity) {
        return switch (stringSeverity) {
            case "Non-Injury Crash" -> CrashSeverity.NONINJURY;
            case "Minor Crash" -> CrashSeverity.MINOR;
            case "Serious Crash" -> CrashSeverity.SERIOUS;
            case "Fatal Crash" -> CrashSeverity.FATAL;
            default -> null;
        };
    }

    /**
     * Converts an int representation of crash severity to the corresponding enum value.
     *
     * @param intSeverity The int representation of crash severity.
     * @return The corresponding CrashSeverity enum value, or null if no match is found.
     */
    public static String intToString(int intSeverity) {
        return switch (intSeverity) {
            case 1 -> "Non-Injury Crash";
            case 4 -> "Minor Crash";
            case 16 -> "Serious Crash";
            case 64 -> "Fatal Crash";
            default -> null;
        };
    }

    /**
     * Gets the integer value associated with the crash severity.
     *
     * @return The integer value of the crash severity.
     */
    public int getValue() {
        return value;
    }
}
