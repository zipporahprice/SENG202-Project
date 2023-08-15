package seng202.team0.models;

public enum CrashSeverity {
    NONINJURY, MINOR, SERIOUS, FATAL;

    public static CrashSeverity stringToCrashSeverity(String stringSeverity) {
        switch(stringSeverity) {
            case "Non-Injury Crash": return CrashSeverity.NONINJURY;
            case "Minor Crash": return CrashSeverity.MINOR;
            case "Serious Crash": return CrashSeverity.SERIOUS;
            case "Fatal Crash": return CrashSeverity.FATAL;
            default: return null;
        }
    }
}
