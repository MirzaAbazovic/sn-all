/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.2009 14:40:11
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.service.iface.IServiceObject;


/**
 *
 */
@SuppressWarnings("unused")
public class UEVTBuilder extends AbstractCCIDModelBuilder<UEVTBuilder, UEVT> implements IServiceObject {
    private Long hvtIdStandort = null;
    @ReferencedEntityId("hvtIdStandort")
    private HVTStandortBuilder hvtStandortBuilder;
    private String uevt = "0204";
    private Integer schwellwert = null;
    private Boolean projektierung = null;
    private Long rackId = null;

    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    public UEVTBuilder withHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
        return this;
    }

    public String getUevt() {
        return uevt;
    }

    public UEVTBuilder withUevt(String uevt) {
        this.uevt = uevt;
        return this;
    }

    public UEVTBuilder withHvtStandortBuilder(HVTStandortBuilder hvtStandortBuilder) {
        this.hvtStandortBuilder = hvtStandortBuilder;
        return this;
    }
}
