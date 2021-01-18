package thermostat.util;

/**
 * A class that contains static unchanging data
 * used throughout the program.
 */
public final class Constants {
    /**
     * Thermostat's default prefix.
     */
    public static String DEFAULT_PREFIX;
    public static String THERMOSTAT_USER_ID;
    public static String THERMOSTAT_AVATAR_URL;

    /**
     * Initialize Thermostat's constants.
     */
    public static void setConstants(String pref, String id, String url) {
        DEFAULT_PREFIX = pref;
        THERMOSTAT_USER_ID = id;
        THERMOSTAT_AVATAR_URL = url;
    }
}