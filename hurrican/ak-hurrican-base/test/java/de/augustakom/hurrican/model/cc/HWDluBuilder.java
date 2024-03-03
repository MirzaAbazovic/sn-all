/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2009 11:00:56
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.cc.hardware.HWDlu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;


/**
 * EntityBuilder for HWDlu objects
 *
 *
 */
@SuppressWarnings("unused")
public class HWDluBuilder extends HWRackBuilder<HWDluBuilder, HWDlu> {

    @Override
    protected void initialize() {
        super.withRackTyp(HWRack.RACK_TYPE_DLU);
    }

    private String dluNumber = "1234";
    private String dluType = "DLUD";
    private HWSwitchBuilder hwSwitchBuilder;
    private String mediaGatewayName = null;
    private String accessController = null;

    public String getDluNumber() {
        return this.dluNumber;
    }

    public HWDluBuilder withDluNumer(String dluNumber) {
        this.dluNumber = dluNumber;
        return this;
    }

    public HWDluBuilder withHwSwitchBuilder(HWSwitchBuilder hwSwitch) {
        this.hwSwitchBuilder = hwSwitch;
        return this;
    }

    public HWDluBuilder withMediaGatewayName(String mediaGatewayName) {
        this.mediaGatewayName = mediaGatewayName;
        return this;
    }

    public HWDluBuilder withAccessController(String accessController) {
        this.accessController = accessController;
        return this;
    }

}
