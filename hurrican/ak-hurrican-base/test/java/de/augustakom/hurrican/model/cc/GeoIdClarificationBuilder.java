/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2011 11:12:21
 */

package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.ReferencedEntityId;


/**
 * EntityBuilder fuer {@link GeoIdClarification} Objekte.
 */
public class GeoIdClarificationBuilder extends EntityBuilder<GeoIdClarificationBuilder, GeoIdClarification> {

    private Long id = randomLong(1);
    @ReferencedEntityId("geoId")
    private GeoIdBuilder geoIdBuilder;
    private Reference status;
    private Reference type;
    private String info = randomString(255);

    @Override
    protected void beforeBuild() {
        if (this.geoIdBuilder == null) {
            this.geoIdBuilder = getBuilder(GeoIdBuilder.class);
        }
    }

    public GeoIdClarificationBuilder withGeoIdBuilder(GeoIdBuilder geoIdBuilder) {
        this.geoIdBuilder = geoIdBuilder;
        return this;
    }

    public GeoIdClarificationBuilder withStatus(Reference status) {
        this.status = status;
        return this;
    }

    public GeoIdClarificationBuilder withType(Reference type) {
        this.type = type;
        return this;
    }

}
