/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.04.13 08:22
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

/**
 *
 */
@SuppressWarnings("unused")
public class AuftragVoIPDN2EGPortBuilder extends
        AbstractCCIDModelBuilder<AuftragVoIPDN2EGPortBuilder, AuftragVoIPDN2EGPort> {

    private EndgeraetPort egPort;
    private Long auftragVoipDnId;
    private Date validFrom;
    private Date validTo;

    public AuftragVoIPDN2EGPortBuilder withEgPort(final EndgeraetPort egPort) {
        this.egPort = egPort;
        return this;
    }

    public AuftragVoIPDN2EGPortBuilder withAuftragVoipDnId(final Long auftragVoipDnId) {
        this.auftragVoipDnId = auftragVoipDnId;
        return this;
    }

    public AuftragVoIPDN2EGPortBuilder withValidFrom(final Date validFrom) {
        this.validFrom = validFrom;
        return this;
    }

    public AuftragVoIPDN2EGPortBuilder withValidTo(final Date validTo) {
        this.validTo = validTo;
        return this;
    }

}
