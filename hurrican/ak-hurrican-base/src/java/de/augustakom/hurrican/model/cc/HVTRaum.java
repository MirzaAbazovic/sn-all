/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 10:23:17
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.shared.iface.HvtIdStandortModel;


/**
 * Modell, um einen HVT-Raum abzubilden.
 *
 *
 */
public class HVTRaum extends AbstractCCIDModel implements HvtIdStandortModel {

    private Long hvtIdStandort = null;
    private String raum = null;

    /**
     * @return hvtIdStandort
     */
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    /**
     * @param hvtIdStandort Festzulegender hvtIdStandort
     */
    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    /**
     * @return raum
     */
    public String getRaum() {
        return raum;
    }

    /**
     * @param raum Festzulegender raum
     */
    public void setRaum(String raum) {
        this.raum = raum;
    }

}


