package de.mnet.wbci.model;

/**
 * Definiert, um was fuer eine Art von WBCI Request es sich handelt.
 */
public enum RequestTyp {

    VA("V", RequestTyp.VA_NAME, "Standard"),
    STR_AUFH_ABG("S", RequestTyp.STR_AUFH_ABG_NAME, "Storno"),
    STR_AUFH_AUF("S", RequestTyp.STR_AUFH_AUF_NAME, "Storno"),
    STR_AEN_ABG("S", RequestTyp.STR_AEN_ABG_NAME, "Storno"),
    STR_AEN_AUF("S", RequestTyp.STR_AEN_AUF_NAME, "Storno"),
    TV("T", RequestTyp.TV_NAME, "Terminverschiebung"),
    UNBEKANNT("", "Unbekannt", "Unbekannt");

    public static final String VA_NAME = "VA";
    public static final String STR_AUFH_ABG_NAME = "STR-AUFH-ABG";
    public static final String STR_AUFH_AUF_NAME = "STR-AUFH-AUF";
    public static final String STR_AEN_ABG_NAME = "STR-AEN-ABG";
    public static final String STR_AEN_AUF_NAME = "STR-AEN-AUF";
    public static final String TV_NAME = "TV";

    private String preAgreementIdCode;
    private String shortName;
    private String longName;

    RequestTyp(String preAgreementIdCode, String shortName, String longName) {
        this.shortName = shortName;
        this.preAgreementIdCode = preAgreementIdCode;
        this.longName = longName;
    }

    /**
     * Maps the requestTypeName (the descriminator value stored within the T_WBCI_REQUEST table to the respective
     * request type.
     *
     * @param requestTypeName the name to map or build from
     * @return the mapped RequestType or null if no match found
     */
    public static RequestTyp buildFromShortName(final String requestTypeName) {
        if (requestTypeName != null) {
            for (RequestTyp typ : RequestTyp.values()) {
                if (!typ.equals(UNBEKANNT) && typ.getShortName().equals(requestTypeName)) {
                    return typ;
                }
            }
        }
        return UNBEKANNT;
    }

    /**
     * converts a {@link String} 'STR_AEN_ABG' to the valid {@link RequestTyp}.
     *
     * @param requestTypeName name of the Type with underscores.
     */
    public static RequestTyp buildFromName(final String requestTypeName) {
        if (requestTypeName != null) {
            for (RequestTyp typ : RequestTyp.values()) {
                if (typ.name().equals(requestTypeName)) {
                    return typ;
                }
            }
        }
        return UNBEKANNT;

    }

    @Override
    public String toString() {
        return name();
    }

    public String getPreAgreementIdCode() {
        return preAgreementIdCode;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    /**
     * @return true if the RequestTyp is a Storno.
     */
    public boolean isStorno() {
        return isStornoAenderung() || isStornoAufhebung();
    }

    public boolean isStornoAenderung() {
        return this.equals(STR_AEN_ABG) || this.equals(STR_AEN_AUF);
    }

    public boolean isStornoAufhebung() {
        return this.equals(STR_AUFH_ABG)  || this.equals(STR_AUFH_AUF);
    }

    /**
     * @return true if the RequestTyp is a Vorabstimmung.
     */
    public boolean isVorabstimmung() {
        return this.equals(VA);
    }

    /**
     * @return true if the RequestTyp is a Terminverschiebung.
     */
    public boolean isTerminverschiebung() {
        return this.equals(TV);
    }
}
