/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2009 18:15:43
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;


/**
 * EntityBuilder fuer {@link PhysikFreigebenView}
 *
 *
 */
@SuppressWarnings("unused")
public class PhysikFreigebenViewBuilder extends EntityBuilder<PhysikFreigebenViewBuilder, PhysikFreigebenView> {

    @ReferencedEntityId("rangierId")
    private RangierungBuilder rangierungBuilder;
    @ReferencedEntityId("auftragId")
    private AuftragTechnikBuilder auftragTechnikBuilder;
    @ReferencedEntityId("endstelleId")
    private EndstelleBuilder endstelleBuilder;
    private Boolean freigeben;
    private String rangierungBemerkung;
    private String clarifyInfo;


    public PhysikFreigebenViewBuilder() {
        setPersist(false);
    }

    public PhysikFreigebenViewBuilder withRangierungBuilder(RangierungBuilder rangierungBuilder) {
        this.rangierungBuilder = rangierungBuilder;
        return this;
    }

    public PhysikFreigebenViewBuilder withAuftragTechnikBuilder(AuftragTechnikBuilder auftragTechnikBuilder) {
        this.auftragTechnikBuilder = auftragTechnikBuilder;
        return this;
    }

    public PhysikFreigebenViewBuilder withEndstelleBuilder(EndstelleBuilder endstelleBuilder) {
        this.endstelleBuilder = endstelleBuilder;
        return this;
    }

    public PhysikFreigebenViewBuilder withFreigeben(Boolean freigeben) {
        this.freigeben = freigeben;
        return this;
    }

    public PhysikFreigebenViewBuilder withClarifyInfo(String clarifyInfo) {
        this.clarifyInfo = clarifyInfo;
        return this;
    }

}


