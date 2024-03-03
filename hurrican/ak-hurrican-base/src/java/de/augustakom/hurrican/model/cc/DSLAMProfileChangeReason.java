/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.04.2007 08:34:21
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell definiert einen Aenderungsgrund fuer ein DSLAM-Profil.
 *
 *
 */
public class DSLAMProfileChangeReason extends AbstractCCIDModel {

    public static final Long CHANGE_REASON_ID_DAILYFILE = Long.valueOf(8);
    public static final Long CHANGE_REASON_ID_AUTOMATIC_SYNC = Long.valueOf(10);
    public static final Long CHANGE_REASON_ID_INIT = Long.valueOf(11);
    public static final Long CHANGE_REASON_ID_MIGRATION = 12L;

    private String name = null;
    private Boolean wholesale;

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    public Boolean getWholesale() {
        return wholesale;
    }

    public void setWholesale(Boolean wholesale) {
        this.wholesale = wholesale;
    }

}


