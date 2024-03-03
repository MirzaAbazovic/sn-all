/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.2012 10:46:15
 */
package de.augustakom.hurrican.model.cc.vento.availability;


/**
 *
 */
public enum VentoTechnologyType {

    ADSL,
    ADSL_POTS,
    ADSL_ISDN,
    ADSL2PLUS,
    ADSL2PLUS_POTS,
    ADSL2PLUS_ISDN,
    FTTX_POTS,
    GPON,
    ISDN,
    POTS,
    RF,
    SDSL,
    SHDSL,
    VDSL2;

    public String value() {
        return name();
    }

    public static VentoTechnologyType fromValue(String v) {
        return valueOf(v);
    }

}


