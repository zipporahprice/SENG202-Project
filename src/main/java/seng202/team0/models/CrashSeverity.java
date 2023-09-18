package seng202.team0.models;

public enum CrashSeverity {
    NONINJURY(1), MINOR(2), SERIOUS(4), FATAL(8);
    private final int value;

    CrashSeverity(int value) {
        this.value = value;
    }

    public static CrashSeverity stringToCrashSeverity(String stringSeverity) {
        switch(stringSeverity) {
            case "Non-Injury Crash": return CrashSeverity.NONINJURY;
            case "Minor Crash": return CrashSeverity.MINOR;
            case "Serious Crash": return CrashSeverity.SERIOUS;
            case "Fatal Crash": return CrashSeverity.FATAL;
            default: return null;
        }
    }

    public int getValue() {
        return value;
    }




}
