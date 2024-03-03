/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2010 10:50:36
 */
package de.augustakom.hurrican.model.cc.housing;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;


/**
 * EntityBuilder fuer {@link HousingRoom} Objekte.
 *
 *
 */
public class HousingRoomBuilder extends EntityBuilder<HousingRoomBuilder, HousingRoom> {

    @SuppressWarnings("unused")
    private String room = randomString(10);
    private Set<HousingParcel> parcels = new HashSet<HousingParcel>();


    @Override
    protected void beforeBuild() {
        super.beforeBuild();
        super.setPersist(false);
    }

    public HousingRoomBuilder withParcel(HousingParcel parcel) {
        parcels.add(parcel);
        return this;
    }

}


