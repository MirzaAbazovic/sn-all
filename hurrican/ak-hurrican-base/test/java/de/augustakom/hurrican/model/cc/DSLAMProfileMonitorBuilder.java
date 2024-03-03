/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.01.2012 09:36:12
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

/**
 * Erstellt Instanzen von {@link DSLAMProfileMonitor}.
 *
 *
 * @since Release 11
 */
@SuppressWarnings("unused")
public class DSLAMProfileMonitorBuilder extends
        AbstractCCIDModelBuilder<DSLAMProfileMonitorBuilder, DSLAMProfileMonitor> {

    private AuftragBuilder auftragBuilder = null;
    private Long auftragId = null;
    private Date monitoringSince = new Date();
    private Date monitoringEnds = new Date();
    private Boolean deleted = Boolean.FALSE;


    public DSLAMProfileMonitorBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public DSLAMProfileMonitorBuilder withMonitoringSince(Date monitoringSince) {
        this.monitoringSince = monitoringSince;
        return this;
    }

    public DSLAMProfileMonitorBuilder withMonitoringEnds(Date monitoringEnds) {
        this.monitoringEnds = monitoringEnds;
        return this;
    }

    public DSLAMProfileMonitorBuilder withDeleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

}
