package de.mnet.wbci.model;

import org.apache.log4j.Logger;

public enum GeschaeftsfallTyp {
    VA_KUE_MRN("VA-KUE-MRN", "Kündigung mit Rufnummernportierung"),
    VA_KUE_ORN("VA-KUE-ORN", "Kündigung ohne Rufnummernportierung"),
    VA_RRNP("VA-RRNP", "Reine Rufnummernportierung"),
    VA_UNBEKANNT(null, null);

    private static final Logger LOGGER = Logger.getLogger(GeschaeftsfallTyp.class);

    public static final String VA_KUE_MRN_NAME = "VA_KUE_MRN";
    public static final String VA_KUE_ORN_NAME = "VA_KUE_ORN";
    public static final String VA_RRNP_NAME = "VA_RRNP";

    /**
     * Ausführliche Beschreibung des Geschäftsfalls, z.B. für einen Tool-Tip.
     */
    private final String longName;

    /**
     * Darstellung des Geschaeftsfalls in der GUI, z.B. für den History-Dialog
     */
    private final String shortName;

    private GeschaeftsfallTyp(String shortName, String longName) {
        this.longName = longName;
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    /**
     * converts a {@link String} 'VA_KUE_MRN' to the valid {@link GeschaeftsfallTyp}.
     *
     * @param geschaeftsfallTypeName name of the Type with underscores.
     */
    public static GeschaeftsfallTyp buildFromName(final String geschaeftsfallTypeName) {
        for (GeschaeftsfallTyp typ : GeschaeftsfallTyp.values()) {
            if (typ.name().equals(geschaeftsfallTypeName)) {
                return typ;
            }
        }
        LOGGER.error("GeschaeftsfallTyp von Request nicht bekannt: " + geschaeftsfallTypeName);
        return VA_UNBEKANNT;
    }
}
