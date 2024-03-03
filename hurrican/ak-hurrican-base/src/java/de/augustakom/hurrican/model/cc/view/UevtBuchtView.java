/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2010 15:02:13
 */

package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.AbstractCCModel;


/**
 *
 */
public class UevtBuchtView extends AbstractCCModel {

    private Long hvtIdStandort = null;
    private String uevt = null;
    private String bucht = null;

    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    public String getUevt() {
        return uevt;
    }

    public void setUevt(String uevt) {
        this.uevt = uevt;
    }

    public String getBucht() {
        return bucht;
    }

    public void setBucht(String bucht) {
        this.bucht = bucht;
    }

}
