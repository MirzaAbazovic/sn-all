/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.07.2004 15:54:34
 */
package de.augustakom.hurrican.model.cc;


/**
 * Model bildet eine (Hardware-)Schnittstelle ab.
 *
 *
 */
public class Schnittstelle extends AbstractCCIDModel {

    /**
     * ID fuer die Schnittstelle 'a/b'
     */
    public static final Long SCHNITTSTELLE_AB = Long.valueOf(7);
    /**
     * ID fuer die Schnittstelle 'S0'
     */
    public static final Long SCHNITTSTELLE_S0 = Long.valueOf(18);
    /**
     * ID fuer die Schnittstelle 'Uk0'
     */
    public static final Long SCHNITTSTELLE_UK0 = Long.valueOf(22);
    /**
     * ID fuer die Schnittstelle 'ADSL'
     */
    public static final Long SCHNITTSTELLE_ADSL = Long.valueOf(8);
    /**
     * ID fuer die Schnittstelle 'ADSL 2+'
     */
    public static final Long SCHNITTSTELLE_ADSL2P = Long.valueOf(32);
    /**
     * ID fuer die Schnittstelle 'SDSL'
     */
    public static final Long SCHNITTSTELLE_SDSL = Long.valueOf(19);
    /**
     * ID fuer die Schnittstelle 'G.SHDSL'
     */
    public static final Long SCHNITTSTELLE_GSHDSL = Long.valueOf(34);
    /**
     * ID fuer die Schnittstelle 'G.SHDSL.bis'
     */
    public static final Long SCHNITTSTELLE_GSHDSL_BIS = Long.valueOf(35);
    /**
     * ID fuer die Schnittstelle 'ATM'
     */
    public static final Long SCHNITTSTELLE_ATM = Long.valueOf(16);
    /**
     * ID fuer die Schnittstelle 'sonstiges'
     */
    public static final Long SCHNITTSTELLE_SONSTIGE = Long.valueOf(30);

    private String schnittstelle = null;

    /**
     * @return Returns the schnittstelle.
     */
    public String getSchnittstelle() {
        return schnittstelle;
    }

    /**
     * @param schnittstelle The schnittstelle to set.
     */
    public void setSchnittstelle(String schnittstelle) {
        this.schnittstelle = schnittstelle;
    }
}


