/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.11.2010 07:49:35
 */
package de.augustakom.hurrican.model.cc.equipment;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.Equipment;


/**
 * EntityBuilder fuer Objekte des Typs {@link HWBaugruppenChangeDluV5}
 */
@SuppressWarnings("unused")
public class HWBaugruppenChangeDluV5Builder extends EntityBuilder<HWBaugruppenChangeDluV5Builder, HWBaugruppenChangeDluV5> {

    private Long hwBgChangeDluId;
    private Equipment dluEquipment;
    private String hwEqn;
    private String v5Port;

    public HWBaugruppenChangeDluV5Builder withHwBaugruppenChangeDluId(Long id) {
        this.hwBgChangeDluId = id;
        return this;
    }

    public HWBaugruppenChangeDluV5Builder withEquipment(Equipment dluEquipment) {
        this.dluEquipment = dluEquipment;
        if ((this.dluEquipment != null) && (this.dluEquipment.getHwEQN() != null)) {
            this.hwEqn = this.dluEquipment.getHwEQN();
        }
        return this;
    }

    public HWBaugruppenChangeDluV5Builder withV5Port(String v5Port) {
        this.v5Port = v5Port;
        return this;
    }
}


