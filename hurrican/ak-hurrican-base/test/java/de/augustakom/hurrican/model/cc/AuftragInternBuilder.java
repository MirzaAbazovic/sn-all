/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2010
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.tools.lang.DateTools;

@SuppressWarnings("unused")
public class AuftragInternBuilder extends AbstractCCIDModelBuilder<AuftragInternBuilder, AuftragIntern> {

    @DontCreateBuilder
    private AuftragBuilder auftragBuilder = null;
    private Long auftragId;

    @DontCreateBuilder
    private HVTStandortBuilder hvtStandortBuilder = null;
    private Long hvtStandortId;

    private Long workingTypeRefId = null;
    private String contactName = "auftragIntern";
    private String contactPhone = null;
    private String contactMail = null;
    private Long extServiceProviderId = null;
    private Date extOrderDate = null;
    private String bedarfsnummer = null;
    private Float workingHours = null;
    private String description = null;
    private Date gueltigVon = DateTools.createDate(2009, 0, 1);
    private Date gueltigBis = DateTools.getHurricanEndDate();
    private String projectDirectory = null;

    @Override
    protected void beforeBuild() {
        if (auftragBuilder == null) {
            auftragBuilder = getBuilder(AuftragBuilder.class);
        }
        auftragBuilder.withAuftragInternBuilder(this);
    }

    public AuftragBuilder getAuftragBuilder() {
        return auftragBuilder;
    }

    public Long getAuftragId() {
        return auftragId;
    }

    public AuftragInternBuilder withAuftragNoOrig(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public AuftragInternBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        if (auftragBuilder.getAuftragInternBuilder() != this) {
            auftragBuilder.withAuftragInternBuilder(this);
        }
        return this;
    }

    public AuftragInternBuilder withHvtStandortBuilder(HVTStandortBuilder hvtStandortBuilder) {
        this.hvtStandortBuilder = hvtStandortBuilder;
        return this;
    }

    public AuftragInternBuilder withHvtStandortId(Long hvtStandortId) {
        this.hvtStandortId = hvtStandortId;
        this.hvtStandortBuilder = null;
        return this;
    }

    public AuftragInternBuilder withContactName(String contactName) {
        this.contactName = contactName;
        return this;
    }

    public AuftragInternBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public AuftragInternBuilder withWorkingTypeRefId(Long workingTypeRefId) {
        this.workingTypeRefId = workingTypeRefId;
        return this;
    }

    /* getters */

    public HVTStandortBuilder getHvtStandortBuilder() {
        return hvtStandortBuilder;
    }


    public Long getHvtStandortId() {
        return hvtStandortId;
    }
}
