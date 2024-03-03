/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.2016
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;

public class HWDpuBuilder extends HWOltChildBuilder<HWDpuBuilder, HWDpu> {

    private String dpuType = HWDpu.DPU_TYPE_MA5811S_AE04;
    private Boolean  reversePower = Boolean.FALSE;

    @Override
    protected void initialize() {
        super.withRackTyp(HWRack.RACK_TYPE_DPU);
    }

    public HWDpuBuilder withDpuType(String dpuType) {
        this.dpuType = dpuType;
        return this;
    }
    public HWDpuBuilder withReversePower(Boolean reversePower) {
        this.reversePower = reversePower;
        return this;
    }
}
