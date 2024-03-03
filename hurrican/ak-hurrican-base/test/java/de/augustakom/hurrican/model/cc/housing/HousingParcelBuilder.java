/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2010 10:52:08
 */
package de.augustakom.hurrican.model.cc.housing;

import de.augustakom.common.model.EntityBuilder;


/**
 * EntityBuilder fuer {@link HousingParcel} Objekte.
 *
 *
 */
@SuppressWarnings("unused")
public class HousingParcelBuilder extends EntityBuilder<HousingParcelBuilder, HousingParcel> {

    private String parcel = randomString(10);
    private Long qm = randomLong(1);

    @Override
    protected void beforeBuild() {
        super.beforeBuild();
        super.setPersist(false);
    }
}


