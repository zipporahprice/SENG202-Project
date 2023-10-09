package seng202.team0.models;

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
    NONINJURY(1), MINOR(2), SERIOUS(4), FATAL(8);
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
        switch(stringSeverity) {
            case "Non-Injury Crash": return CrashSeverity.NONINJURY;
            case "Minor Crash": return CrashSeverity.MINOR;
            case "Serious Crash": return CrashSeverity.SERIOUS;
            case "Fatal Crash": return CrashSeverity.FATAL;
            default: return null;
        }
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
