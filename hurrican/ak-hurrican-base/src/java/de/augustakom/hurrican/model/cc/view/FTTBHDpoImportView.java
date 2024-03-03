/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.11.2014
 */
package de.augustakom.hurrican.model.cc.view;

/**
 * View-Modell, fuer den Import der DPOs
 */
public class FTTBHDpoImportView extends OltChildImportView{

    private String chassisIdentifier;
    private String chassisSlot;

    public String getChassisIdentifier() {
        return chassisIdentifier;
    }

    public void setChassisIdentifier(String chassisIdentifier) {
        this.chassisIdentifier = chassisIdentifier;
    }

    public String getChassisSlot() {
        return chassisSlot;
    }

    public void setChassisSlot(String chassisSlot) {
        this.chassisSlot = chassisSlot;
    }

}
