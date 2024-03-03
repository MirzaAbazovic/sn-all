/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2010 09:33:51
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.ReferencedEntityId;


/**
 * EntityBuilder fuer {@link RSMonitorConfig} Objekte.
 */
@SuppressWarnings("unused")
public class RSMonitorConfigBuilder extends EntityBuilder<RSMonitorConfigBuilder, RSMonitorConfig> {

    private Long id = null;
    @ReferencedEntityId("hvtIdStandort")
    private HVTStandortBuilder hvtStandortBuilder;
    private Long monitorType = null;
    private Long physiktyp = null;
    private Long physiktypAdd = null;
    private String eqRangSchnittstelle = null;
    private String eqUEVT = null;
    private Integer minCount = null;
    private String userw = null;
    private Date datew = null;
    private Boolean alarmierung = null;
    private Integer dayCount = null;

    public RSMonitorConfigBuilder withHvtStandortBuilder(HVTStandortBuilder hvtStandortBuilder) {
        this.hvtStandortBuilder = hvtStandortBuilder;
        return this;
    }

    public RSMonitorConfigBuilder withAlarmierung(Boolean alarmierung) {
        this.alarmierung = alarmierung;
        return this;
    }

    public RSMonitorConfigBuilder withMinCount(Integer minCount) {
        this.minCount = minCount;
        return this;
    }

    public RSMonitorConfigBuilder withDayCount(Integer dayCount) {
        this.dayCount = dayCount;
        return this;
    }

    public RSMonitorConfigBuilder withPhysiktyp(Long physiktyp) {
        this.physiktyp = physiktyp;
        return this;
    }

}


