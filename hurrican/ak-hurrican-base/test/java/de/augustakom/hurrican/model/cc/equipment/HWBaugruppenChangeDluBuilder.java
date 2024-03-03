/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.11.2010 07:53:07
 */
package de.augustakom.hurrican.model.cc.equipment;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.HWDluBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;


/**
 * EntityBuilder fuer Objekte des Typs {@link HWBaugruppenChangeDlu}.
 */
@SuppressWarnings("unused")
public class HWBaugruppenChangeDluBuilder extends EntityBuilder<HWBaugruppenChangeDluBuilder, HWBaugruppenChangeDlu> {

    private Long id = randomLong(99999999);
    private HWDluBuilder dluRackOldBuilder;
    private String dluNumberNew;
    private HWSwitch dluSwitchNew;
    private String dluMediaGatewayNew;
    private String dluAccessControllerNew;

    public HWBaugruppenChangeDluBuilder withDluBuilder(HWDluBuilder dluBuilder) {
        this.dluRackOldBuilder = dluBuilder;
        return this;
    }

    public HWBaugruppenChangeDluBuilder withDluNumberNew(String dluNumberNew) {
        this.dluNumberNew = dluNumberNew;
        return this;
    }

    public HWBaugruppenChangeDluBuilder withDluSwitchNew(HWSwitch dluSwitchNew) {
        this.dluSwitchNew = dluSwitchNew;
        return this;
    }

    public HWBaugruppenChangeDluBuilder withDluMediaGatewayNew(String dluMediaGatewayNew) {
        this.dluMediaGatewayNew = dluMediaGatewayNew;
        return this;
    }

    public HWBaugruppenChangeDluBuilder withDluAccessControllerNew(String dluAccessControllerNew) {
        this.dluAccessControllerNew = dluAccessControllerNew;
        return this;
    }

}


