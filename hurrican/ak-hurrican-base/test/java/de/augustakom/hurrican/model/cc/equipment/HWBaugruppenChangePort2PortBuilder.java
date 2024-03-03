/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.04.2010 10:20:20
 */
package de.augustakom.hurrican.model.cc.equipment;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;


/**
 * Entity Builder fuer {@link HWBaugruppenChangePort2Port} Objekte.
 *
 *
 */
@SuppressWarnings("unused")
public class HWBaugruppenChangePort2PortBuilder extends EntityBuilder<HWBaugruppenChangePort2PortBuilder, HWBaugruppenChangePort2Port> {

    private Equipment equipmentOld;
    private Equipment equipmentOldIn;
    private Equipment equipmentNew;
    private EqStatus eqStateOrigOld = EqStatus.rang;
    private EqStatus eqStateOrigNew = EqStatus.frei;
    private Freigegeben rangStateOrigOld;
    private Freigegeben rangStateOrigNew;

    public HWBaugruppenChangePort2PortBuilder withEquipmentOld(Equipment equipmentOld) {
        this.equipmentOld = equipmentOld;
        return this;
    }

    public HWBaugruppenChangePort2PortBuilder withEquipmentOldIn(Equipment equipmentOldIn) {
        this.equipmentOldIn = equipmentOldIn;
        return this;
    }

    public HWBaugruppenChangePort2PortBuilder withEquipmentNew(Equipment equipmentNew) {
        this.equipmentNew = equipmentNew;
        return this;
    }

    public HWBaugruppenChangePort2PortBuilder withEqStateOrigOld(EqStatus eqStateOrigOld) {
        this.eqStateOrigOld = eqStateOrigOld;
        return this;
    }

    public HWBaugruppenChangePort2PortBuilder withEqStateOrigNew(EqStatus eqStateOrigNew) {
        this.eqStateOrigNew = eqStateOrigNew;
        return this;
    }

    public HWBaugruppenChangePort2PortBuilder withRangStateOrigOld(Freigegeben rangStateOrigOld) {
        this.rangStateOrigOld = rangStateOrigOld;
        return this;
    }

}


