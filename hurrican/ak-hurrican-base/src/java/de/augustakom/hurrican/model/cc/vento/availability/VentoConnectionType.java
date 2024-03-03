/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.2012 10:45:54
 */
package de.augustakom.hurrican.model.cc.vento.availability;


/**
 *
 */
public enum VentoConnectionType {

    TAL_HVT,
    FTTB,
    FTTC,
    FTTH,
    FTTB_H,
    GEWOFAG;

    public String value() {
        return name();
    }

    public static VentoConnectionType fromValue(String v) {
        return valueOf(v);
    }

}


