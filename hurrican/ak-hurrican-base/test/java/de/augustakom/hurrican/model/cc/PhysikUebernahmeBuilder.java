/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2009 08:11:49
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.model.ReferencedEntityId;

/**
 * Entity Builder for PhysikUebernahme objects
 */
@SuppressWarnings("unused")
public class PhysikUebernahmeBuilder extends AbstractCCIDModelBuilder<PhysikUebernahmeBuilder, PhysikUebernahme> {

    { id = getLongId(); }

    private Long vorgang = randomLong(Integer.MAX_VALUE / 2, Integer.MAX_VALUE);
    @ReferencedEntityId("auftragIdA")
    private AuftragBuilder auftragABuilder = null;
    @ReferencedEntityId("auftragIdB")
    private AuftragBuilder auftragBBuilder = null;
    private Integer kriterium = PhysikUebernahme.KRITERIUM_ALT_NEU;
    private Long aenderungstyp = PhysikaenderungsTyp.STRATEGY_DSL_KREUZUNG;
    @DontCreateBuilder
    private VerlaufBuilder verlaufBuilder = null;

    public PhysikUebernahmeBuilder withVorgang(Long vorgang) {
        this.vorgang = vorgang;
        return this;
    }

    public PhysikUebernahmeBuilder withAuftragABuilder(AuftragBuilder auftragABuilder) {
        this.auftragABuilder = auftragABuilder;
        return this;
    }

    public PhysikUebernahmeBuilder withAuftragBBuilder(AuftragBuilder auftragBBuilder) {
        this.auftragBBuilder = auftragBBuilder;
        return this;
    }

    public PhysikUebernahmeBuilder withAenderungstyp(Long aenderungstyp) {
        this.aenderungstyp = aenderungstyp;
        return this;
    }

    public PhysikUebernahmeBuilder withVerlaufBuilder(VerlaufBuilder verlaufBuilder) {
        this.verlaufBuilder = verlaufBuilder;
        return this;
    }
}
