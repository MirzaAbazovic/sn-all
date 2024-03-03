package de.mnet.wbci.model;

public enum CarrierCode {

    MNET("DEU.MNET"),
    DTAG("DEU.DTAG"),
    VODAFONE("DEU.VFDE"),
    EINS_UND_EINS("DEU.1UND1"),
    KABEL_DEUTSCHLAND("DEU.KDVS"),
    TELEFONICA("DEU.TEFGER"),
    DEU_QSC("DEU.QSC"),
    RKOM("DEU.RKOM"),
    SIPGATE("DEU.SG"),
    COLT("DEU.COLTDE"),
    TELE2("DEU.TEL2DE"),
    HL_KOMM("DEU.HLK"),
    UNITY("DEU.UMHESS"),
    SUEC_DACOR("DEU.DACOR"),
    HFO_TELECOM("DEU.HFO"),
    KOMRO("DEU.KOMRO"),
    DCC("DEU.DCC"),
    STERN("DEU.STERN"),
    BRANDL("DEU.BRANDL"),
    MEDIAPORT("DEU.CSURF"),
    AMPLUS("DEU.AMPLUS"),
    BISP("DEU.BISP"),
    ECOTEL("DEU.ECOTEL");

    private final String iTUCarrierCode;

    CarrierCode(String iTUCarrierCode) {
        this.iTUCarrierCode = iTUCarrierCode;
    }

    /**
     * @return Country-Code und Carrier-Code after ITU M.1400
     */
    public String getITUCarrierCode() {
        return iTUCarrierCode;
    }

    public static CarrierCode fromITUCarrierCode(String iTUCarrierCode) {
        for (CarrierCode carrierCode : values()) {
            if (carrierCode.getITUCarrierCode().equals(iTUCarrierCode)) {
                return carrierCode;
            }
        }

        throw new IllegalArgumentException("There exists no CarrierCode for name '" + iTUCarrierCode + "'");
    }

    public boolean isWholesaleCarrier() {
        return this.equals(EINS_UND_EINS);
    }

    public boolean isMnet() {
        return this.equals(MNET);
    }
}
