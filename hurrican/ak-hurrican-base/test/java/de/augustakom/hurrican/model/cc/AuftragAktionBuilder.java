/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.03.2012 10:00:14
 */
package de.augustakom.hurrican.model.cc;

import java.time.*;

import de.augustakom.hurrican.model.cc.AuftragAktion.AktionType;

/**
 *
 */
@SuppressWarnings("unused")
public class AuftragAktionBuilder extends AbstractCCIDModelBuilder<AuftragAktionBuilder, AuftragAktion> {
    private AuftragBuilder auftragBuilder;
    private AktionType action;
    private LocalDate desiredExecutionDate;
    private Boolean cancelled = Boolean.FALSE;
    private AuftragBuilder previousAuftragBuilder;

    public AuftragAktionBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public AuftragAktionBuilder withAction(AktionType action) {
        this.action = action;
        return this;
    }

    public AuftragAktionBuilder withDesiredExecutionDate(LocalDate desiredExecutionDate) {
        this.desiredExecutionDate = desiredExecutionDate;
        return this;
    }

    public AuftragAktionBuilder withCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
        return this;
    }

    public AuftragAktionBuilder withPreviousAuftragBuilder(AuftragBuilder previousAuftragBuilder) {
        this.previousAuftragBuilder = previousAuftragBuilder;
        return this;
    }
}


