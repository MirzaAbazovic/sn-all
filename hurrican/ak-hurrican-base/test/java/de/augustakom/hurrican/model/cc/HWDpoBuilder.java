/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.2014
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWRack;

public class HWDpoBuilder extends HWOltChildBuilder<HWDpoBuilder, HWDpo> {

    private String dpoType = HWDpo.DPO_TYPE_MA5651;
    private String chassisIdentifier = null;
    private String chassisSlot = null;

    @Override
    protected void initialize() {
        super.withRackTyp(HWRack.RACK_TYPE_DPO);
    }

    public HWDpoBuilder withDpoType(String dpoType) {
        this.dpoType = dpoType;
        return this;
    }

    public HWDpoBuilder withChassisIdentifier(String chassisIdentifier) {
        this.chassisIdentifier = chassisIdentifier;
        return this;
    }

    public HWDpoBuilder withChassisSlot(String chassisSlot) {
        this.chassisSlot = chassisSlot;
        return this;
    }

}
