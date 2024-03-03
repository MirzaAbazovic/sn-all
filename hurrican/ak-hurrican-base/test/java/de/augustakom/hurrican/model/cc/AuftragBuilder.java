/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 13:12:08
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.service.iface.IServiceObject;


/**
 *
 */
public class AuftragBuilder extends AbstractCCIDModelBuilder<AuftragBuilder, Auftrag> implements IServiceObject {

    private Long kundeNo = randomLong(Integer.MAX_VALUE / 2);
    private AuftragDatenBuilder auftragDatenBuilder = null;
    @DontCreateBuilder
    private AuftragTechnikBuilder auftragTechnikBuilder = null;
    @DontCreateBuilder
    private AuftragInternBuilder auftragInternBuilder = null;


    /**
     * We want to have valid AuftragDaten for the Auftrag
     */
    @Override
    protected void afterBuild(Auftrag auftrag) {
        if (auftragDatenBuilder == null) {
            auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class);
        }
        if (!auftragDatenBuilder.isBuilding()) {
            auftragDatenBuilder.withAuftragBuilder(this).get();
        }
        if ((auftragTechnikBuilder != null) && !auftragTechnikBuilder.isBuilding()) {
            auftragTechnikBuilder.withAuftragBuilder(this).get();
        }
        if ((auftragInternBuilder != null) && !auftragInternBuilder.isBuilding()) {
            auftragInternBuilder.withAuftragBuilder(this).get();
        }
    }


    public AuftragDatenBuilder getAuftragDatenBuilder() {
        return auftragDatenBuilder;
    }

    public AuftragTechnikBuilder getAuftragTechnikBuilder() {
        return auftragTechnikBuilder;
    }

    public AuftragInternBuilder getAuftragInternBuilder() {
        return auftragInternBuilder;
    }

    public AuftragBuilder withAuftragDatenBuilder(AuftragDatenBuilder auftragDatenBuilder) {
        this.auftragDatenBuilder = auftragDatenBuilder;
        if (auftragDatenBuilder.getAuftragBuilder() != this) {
            auftragDatenBuilder.withAuftragBuilder(this);
        }
        return this;
    }

    public AuftragBuilder withAuftragTechnikBuilder(AuftragTechnikBuilder auftragTechnikBuilder) {
        this.auftragTechnikBuilder = auftragTechnikBuilder;
        if (auftragTechnikBuilder.getAuftragBuilder() != this) {
            auftragTechnikBuilder.withAuftragBuilder(this);
        }
        return this;
    }

    public AuftragBuilder withAuftragInternBuilder(AuftragInternBuilder auftragInternBuilder) {
        this.auftragInternBuilder = auftragInternBuilder;
        if (auftragInternBuilder.getAuftragBuilder() != this) {
            auftragInternBuilder.withAuftragBuilder(this);
        }
        return this;
    }

    public Long getKundeNo() {
        return kundeNo;
    }

    public AuftragBuilder withKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
        return this;
    }
}
