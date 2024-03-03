/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 06.07.2010 13:43:37
  */

package de.augustakom.hurrican.model.cc;

/**
 *
 */
public enum Uebertragungsverfahren {
    // Bei Erweiterung Trigger TRBIU_EQUIPMENT (equipmentTrigger.sql) anpassen

    N01(false, "001 TAL ISDN"),
    H01(true, "002 TAL DSL"),
    H02(true, "002 TAL DSL"),
    H03(true, "002 TAL DSL"),
    H04(true, "002 TAL DSL"),
    H05(true, "002 TAL DSL"),
    H07(true, "002 TAL DSL"),
    H08(true, "002 TAL DSL"),
    H11(true, "002 TAL DSL"),
    H13(true, "002 TAL DSL"),
    H16(true, "002 TAL DSL"),
    H18(true, "003 TAL VDSL"),
    LWL(true, "021 Sonstiges");

    private boolean hochbit;
    private String wbciCode;

    /**
     * @param hochbit  Flag definiert, ob es sich um ein hochbit- (true) oder niederbit-ratiges (false)
     *                 Uebertragungsverfahren handelt
     * @param wbciCode Gibt den Technologiebezeichner fuer die WBCI Schnittstelle an
     */
    private Uebertragungsverfahren(boolean hochbit, String wbciCode) {
        this.hochbit = hochbit;
        this.wbciCode = wbciCode;
    }

    public boolean isHochbit() {
        return hochbit;
    }

    public String getWbciCode() {
        return wbciCode;
    }

    public static Uebertragungsverfahren getUetvAndAcceptDslamProfileUetvNames(String uetv) {
        if (DSLAMProfile.UETV_ADSL2PLUS.equals(uetv)) {
            return H13;
        }
        else if (DSLAMProfile.UETV_ADSL.equals(uetv)) {
            return H04;
        }
        return Uebertragungsverfahren.valueOf(uetv);
    }

}
