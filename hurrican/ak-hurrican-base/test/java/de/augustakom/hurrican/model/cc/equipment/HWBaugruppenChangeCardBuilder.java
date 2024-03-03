/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.04.2010 10:28:14
 */
package de.augustakom.hurrican.model.cc.equipment;

import java.util.*;
import com.google.common.collect.Sets;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;


/**
 * EntityBuilder fuer {@link HWBaugruppenChangeCard} Objekte.
 */
@SuppressWarnings("unused")
public class HWBaugruppenChangeCardBuilder extends EntityBuilder<HWBaugruppenChangeCardBuilder, HWBaugruppenChangeCard> {

    private TreeSet<HWBaugruppe> hwBaugruppenNew = Sets.newTreeSet();

    private TreeSet<HWBaugruppe> hwBaugruppenSource = Sets.newTreeSet();


    public HWBaugruppenChangeCardBuilder withHWBaugruppeNew(HWBaugruppe hwBaugruppeNew) {
        this.hwBaugruppenNew.add(hwBaugruppeNew);
        return this;
    }

    public HWBaugruppenChangeCardBuilder withHwBaugruppenSource(HWBaugruppe hwBaugruppeSource) {
        hwBaugruppenSource.add(hwBaugruppeSource);
        return this;
    }
}


