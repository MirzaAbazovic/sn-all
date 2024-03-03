/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2010 10:34:28
 */
package de.augustakom.hurrican.model.cc.housing;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;


/**
 * Entity-Builder fuer {@link HousingBuilding} Objekte.
 *
 *
 */
@SuppressWarnings("unused")
public class HousingBuildingBuilder extends EntityBuilder<HousingBuildingBuilder, HousingBuilding> {


    private String building = randomString(20);
    private CCAddress address;
    private Set<HousingFloor> floors = new HashSet<HousingFloor>();

    public HousingBuildingBuilder withCCAddress(CCAddress address) {
        this.address = address;
        return this;
    }

    public HousingBuildingBuilder withFloor(HousingFloor floor) {
        this.floors.add(floor);
        return this;
    }

}


