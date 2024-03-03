package de.mnet.wbci.config;

/**
 * Interface zur Definition von verschiedenen Konstanten, die in der WBCI Umgebung benoetigt werden.
 */
public interface WbciConstants {

    /**
     * VorabstimmungsId-Prefix, das von Atlas benötigt wird, um die Nachrichten an das richtige System routen zu können
     * (siehe WBAUF-01-46) - in diesem Fall fuer Hurrican.
     */
    public static final String VA_ROUTING_PREFIX_HURRICAN = "H";

    /**
     * User role for WBCI super user. Respectively found in user roles in database.
     */
    public static final String WBCI_SUPER_USER_ROLE = "wbci.superuser";

    public static final String VA_ROUTING_PREFIX_FAX = "F";

    public static boolean isFaxRouting(String vorabstimmungsID) {
        /*Regex for checking FAX IDs like e.g.
        DEU.MNET.VF00087423
        DTAG.NEU.SF00075434
        AC1.123.TF000123daw*/
        String regex = "\\S+\\.\\S+\\.\\wF\\S+";
        return vorabstimmungsID.matches(regex);
    }

}


