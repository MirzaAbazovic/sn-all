/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2015 08:31
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.ReferencedEntityId;

/**
 *
 */
public class Produkt2DslamProfileBuilder extends EntityBuilder<Produkt2DslamProfileBuilder, Produkt2DSLAMProfile> {

    @ReferencedEntityId("prodId")
    private ProduktBuilder produktBuilder;
    private DSLAMProfileBuilder dslamProfileBuilder;

    public Produkt2DslamProfileBuilder withProduktBuilder(final ProduktBuilder produktBuilder) {
        this.produktBuilder = produktBuilder;
        return this;
    }

    public Produkt2DslamProfileBuilder withDslamProfileBuilder(final DSLAMProfileBuilder dslamProfileBuilder)  {
        this.dslamProfileBuilder = dslamProfileBuilder;
        return this;
    }

}
