/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.04.2010 10:25:28
 */
package de.augustakom.hurrican.model.cc.equipment;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;


/**
 * EntityBuilder fuer {@link HWBaugruppenChangeBgType} Objekte.
 */
@SuppressWarnings("unused")
public class HWBaugruppenChangeBgTypeBuilder extends EntityBuilder<HWBaugruppenChangeBgTypeBuilder, HWBaugruppenChangeBgType> {

    private HWBaugruppe hwBaugruppe;
    private HWBaugruppenTyp hwBaugruppenTypNew;

    public HWBaugruppenChangeBgTypeBuilder withHWBaugruppe(HWBaugruppe hwBaugruppe) {
        this.hwBaugruppe = hwBaugruppe;
        return this;
    }

    public HWBaugruppenChangeBgTypeBuilder withHWBaugruppenTypNew(HWBaugruppenTyp hwBaugruppenTypNew) {
        this.hwBaugruppenTypNew = hwBaugruppenTypNew;
        return this;
    }

}


