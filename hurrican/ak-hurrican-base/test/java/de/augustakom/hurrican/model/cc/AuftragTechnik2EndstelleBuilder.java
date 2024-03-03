/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2009 11:11:05
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.DontCreateBuilder;


/**
 *
 */
public class AuftragTechnik2EndstelleBuilder extends AbstractCCIDModelBuilder<AuftragTechnik2EndstelleBuilder, AuftragTechnik2Endstelle> {

    // Properties not in Entity, just for building
    @DontCreateBuilder
    private AuftragTechnikBuilder auftragTechnikBuilder;
    @DontCreateBuilder
    private EndstelleBuilder endstelleBuilder;


    @Override
    protected void afterBuild(AuftragTechnik2Endstelle entity) {
        if ((auftragTechnikBuilder != null) && !auftragTechnikBuilder.isBuilding()) {
            auftragTechnikBuilder.get();
        }
        if ((endstelleBuilder != null) && !endstelleBuilder.isBuilding()) {
            endstelleBuilder.get();
        }
    }


    public AuftragTechnikBuilder getAuftragTechnikBuilder() {
        return auftragTechnikBuilder;
    }

    public EndstelleBuilder getEndstelleBuilder() {
        return endstelleBuilder;
    }


    public AuftragTechnik2EndstelleBuilder withAuftragTechnikBuilder(AuftragTechnikBuilder auftragTechnikBuilder) {
        this.auftragTechnikBuilder = auftragTechnikBuilder;
        if (auftragTechnikBuilder.getAuftragTechnik2EndstelleBuilder() != this) {
            auftragTechnikBuilder.withAuftragTechnik2EndstelleBuilder(this);
        }
        return this;
    }

    public AuftragTechnik2EndstelleBuilder withEndstelleBuilder(EndstelleBuilder endstelleBuilder) {
        this.endstelleBuilder = endstelleBuilder;
        return this;
    }
}
