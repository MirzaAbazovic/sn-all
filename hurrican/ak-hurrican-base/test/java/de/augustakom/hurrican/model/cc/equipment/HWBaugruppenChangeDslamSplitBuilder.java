/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.04.2010 12:08:59
 */
package de.augustakom.hurrican.model.cc.equipment;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;


/**
 * EntityBuilder fuer {@link HWBaugruppenChangeDslamSplit} Objekte.
 */
@SuppressWarnings("unused")
public class HWBaugruppenChangeDslamSplitBuilder extends EntityBuilder<HWBaugruppenChangeDslamSplitBuilder, HWBaugruppenChangeDslamSplit> {

    private HWRack hwRackOld;
    private HWSubrack hwSubrackOld;
    private HWRack hwRackNew;
    private HWSubrack hwSubrackNew;

    public HWBaugruppenChangeDslamSplitBuilder withHwRackOld(HWRack hwRackOld) {
        this.hwRackOld = hwRackOld;
        return this;
    }

    public HWBaugruppenChangeDslamSplitBuilder withHwSubrackOld(HWSubrack hwSubrackOld) {
        this.hwSubrackOld = hwSubrackOld;
        return this;
    }

    public HWBaugruppenChangeDslamSplitBuilder withHwRackNew(HWRack hwRackNew) {
        this.hwRackNew = hwRackNew;
        return this;
    }

    public HWBaugruppenChangeDslamSplitBuilder withHwSubrackNew(HWSubrack hwSubrackNew) {
        this.hwSubrackNew = hwSubrackNew;
        return this;
    }

}


