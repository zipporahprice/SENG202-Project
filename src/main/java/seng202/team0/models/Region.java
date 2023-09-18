package seng202.team0.models;

/**
 * Region enum represents different regions in New Zealand associated with a crash object.
 * It provides a mapping between string representations of region names and enum constants.
 *
 * @author Zipporah Price
 */
public enum Region {

    AUCKLAND("Auckland"),
    BAYOFPLENTY("Bay of Plenty"),
    CANTERBURY("Canterbury"),
    GISBORNE("Gisborne"),
    HAWKESBAY("Hawke's Bay"),
    MANAWATUWHANGANUI("Manawatū-Whanganui"),
    MARLBOROUGH("Marlborough"),
    NELSON("Nelson"),
    NORTHLAND("Northland"),
    OTAGO("Otago"),
    SOUTHLAND("Southland"),
    TARANAKI("Taranaki"),
    TASMAN("Tasman"),
    WAIKATO("Waikato"),
    WELLINGTON("Wellington"),
    WESTCOAST("West Coast"),
    NULL("Null");

    private final String name;

    Region (String name) {
        this.name = name;
    }

    /**
     * Converts a string representation of region into the corresponding Region enum constant.
     *
     * @param stringRegion The string representation of the region.
     * @return The Region enum constant representing the given region string, or null if not found.
     */
    public static Region stringToRegion(String stringRegion) {
        switch(stringRegion) {
            case "Auckland Region": return Region.AUCKLAND;
            case "Bay of Plenty Region": return Region.BAYOFPLENTY;
            case "Canterbury Region": return Region.CANTERBURY;
            case "Gisborne Region": return Region.GISBORNE;
            case "Hawke's Bay Region": return Region.HAWKESBAY;
            case "ManawatÅ«-Whanganui Region": return Region.MANAWATUWHANGANUI;
            case "Marlborough Region": return Region.MARLBOROUGH;
            case "Nelson Region": return Region.NELSON;
            case "Northland Region": return Region.NORTHLAND;
            case "Otago Region": return Region.OTAGO;
            case "Southland Region": return Region.SOUTHLAND;
            case "Taranaki Region": return Region.TARANAKI;
            case "Tasman Region": return Region.TASMAN;
            case "Waikato Region": return Region.WAIKATO;
            case "Wellington Region": return Region.WELLINGTON;
            case "West Coast Region": return Region.WESTCOAST;
            default: return Region.NULL;
        }
    }

    public String getName() {
        return name;
    }

}
