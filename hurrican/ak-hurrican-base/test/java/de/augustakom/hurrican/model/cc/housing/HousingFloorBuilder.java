/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2010 10:39:26
 */
package de.augustakom.hurrican.model.cc.housing;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;


/**
 * EntityBuilder fuer {@link HousingFloor} Objekte.
 *
 *
 */
@SuppressWarnings("unused")
public class HousingFloorBuilder extends EntityBuilder<HousingFloorBuilder, HousingFloor> {

    private Long housingBuildingId;
    private String floor = randomString(10);
    private Set<HousingRoom> rooms = new HashSet<HousingRoom>();


    @Override
    protected void beforeBuild() {
        super.beforeBuild();
        super.setPersist(false);
    }

    public HousingFloorBuilder withRoom(HousingRoom room) {
        this.rooms.add(room);
        return this;
    }

}


