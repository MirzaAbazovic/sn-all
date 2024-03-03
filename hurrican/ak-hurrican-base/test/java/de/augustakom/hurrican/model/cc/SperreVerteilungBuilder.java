/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 15:34:19
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.service.iface.IServiceObject;


/**
 *
 */
public class SperreVerteilungBuilder extends EntityBuilder<SperreVerteilungBuilder, SperreVerteilung> implements IServiceObject {

    private ProduktBuilder produktBuilder = null;
    private AbteilungBuilder abteilungBuilder = null;

    public AbteilungBuilder getAbteilungBuilder() {
        return abteilungBuilder;
    }

    public ProduktBuilder getProduktBuilder() {
        return produktBuilder;
    }


    public SperreVerteilungBuilder withProduktBuilder(ProduktBuilder produktBuilder) {
        this.produktBuilder = produktBuilder;
        return this;
    }


    public SperreVerteilungBuilder withAbteilungBuilder(AbteilungBuilder abteilungBuilder) {
        this.abteilungBuilder = abteilungBuilder;
        return this;
    }

}
