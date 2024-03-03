/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2010 16:28:13
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.ReferencedEntityId;


/**
 * EntityBuilder fuer {@link RsmPortUsage} Objekte.
 */
@SuppressWarnings("unused")
public class RsmPortUsageBuilder extends EntityBuilder<RsmPortUsageBuilder, RsmPortUsage> {

    private Integer year;
    private Integer month;
    @ReferencedEntityId("hvtIdStandort")
    private HVTStandortBuilder hvtStandortBuilder;
    private Long physikTypId;
    private Long physikTypIdAdditional;
    private Long portsUsed;
    private Integer portsCancelled;
    private Integer diffCount;

    public RsmPortUsageBuilder withYear(Integer year) {
        this.year = year;
        return this;
    }

    public RsmPortUsageBuilder withMonth(Integer month) {
        this.month = month;
        return this;
    }

    public RsmPortUsageBuilder withDiffCount(Integer diffCount) {
        this.diffCount = diffCount;
        return this;
    }

    public RsmPortUsageBuilder withHvtStandortBuilder(HVTStandortBuilder hvtStandortBuilder) {
        this.hvtStandortBuilder = hvtStandortBuilder;
        return this;
    }

    public RsmPortUsageBuilder withPhysikTypId(Long physikTypId) {
        this.physikTypId = physikTypId;
        return this;
    }

}


