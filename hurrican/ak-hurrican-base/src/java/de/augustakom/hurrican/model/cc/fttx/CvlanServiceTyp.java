/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2012 09:30:29
 */
package de.augustakom.hurrican.model.cc.fttx;


/**
 * Enum mit den moeglichen {@link CVlan} Typen.
 */
public enum CvlanServiceTyp {

    HSI(CVlanType.UNICAST),
    HSIPLUS(CVlanType.UNICAST),
    VOIP(CVlanType.UNICAST),
    POTS(CVlanType.UNICAST),
    IAD(CVlanType.UNICAST),
    MC(CVlanType.MULTICAST),
    VOD(CVlanType.UNICAST);

    private CVlanType type;

    private CvlanServiceTyp(CVlanType type) {
        this.type = type;
    }

    public CVlanType getType() {
        return this.type;
    }

    public static enum CVlanType {
        MULTICAST,
        UNICAST
    }

}


