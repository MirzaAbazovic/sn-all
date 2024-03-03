/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.11.2014
 */
package de.augustakom.hurrican.model.cc.view;

/**
 * View-Modell, fuer den Import der DPUs
 */
public class FTTBDpuImportView extends OltChildImportView{

    private Boolean reversePower;

    public Boolean getReversePower() {
        return reversePower;
    }

    public void setReversePower(Boolean reversePower) {
        this.reversePower = reversePower;
    }
}
