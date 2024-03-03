/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2010 16:31:51
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.ReferencedEntityId;

/**
 * EntityBuilder fuer {@link RsmRangCount} Objekte.
 */
@SuppressWarnings("unused")
public class RsmRangCountBuilder extends EntityBuilder<RsmRangCountBuilder, RsmRangCount> {

    @ReferencedEntityId("hvtIdStandort")
    private HVTStandortBuilder hvtStandortBuilder;
    private Long physiktyp = null;
    private Long physiktypAdd = null;
    private Integer belegt = Integer.valueOf(0);
    private Integer frei = Integer.valueOf(0);
    private Integer freigabebereit = Integer.valueOf(0);
    private Integer defekt = Integer.valueOf(0);
    private Integer imAufbau = Integer.valueOf(0);
    private Integer vorhanden = Integer.valueOf(0);
    private Integer portReach = null;
    private Float averageUsage = null;

    public RsmRangCountBuilder withHvtStandortBuilder(HVTStandortBuilder hvtStandortBuilder) {
        this.hvtStandortBuilder = hvtStandortBuilder;
        return this;
    }

    public RsmRangCountBuilder withFrei(Integer frei) {
        this.frei = frei;
        return this;
    }

    public RsmRangCountBuilder withImAufbau(Integer imAufbau) {
        this.imAufbau = imAufbau;
        return this;
    }

    public RsmRangCountBuilder withPortReach(Integer portReach) {
        this.portReach = portReach;
        return this;
    }

    public RsmRangCountBuilder withPhysiktyp(Long physiktyp) {
        this.physiktyp = physiktyp;
        return this;
    }

    public RsmRangCountBuilder withVorhanden(Integer vorhanden) {
        this.vorhanden = vorhanden;
        return this;
    }

    public RsmRangCountBuilder withDefekt(Integer defekt) {
        this.defekt = defekt;
        return this;
    }

    public RsmRangCountBuilder withBelegt(Integer belegt) {
        this.belegt = belegt;
        return this;
    }

    public RsmRangCountBuilder withFreigabebereit(Integer freigabebereit) {
        this.freigabebereit = freigabebereit;
        return this;
    }

}
