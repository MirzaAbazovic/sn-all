/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2009 16:34:04
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import com.google.common.collect.Lists;

import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.service.iface.IServiceObject;

/**
 *
 */
@SuppressWarnings("unused")
public class AuftragVoIPDNBuilder extends AbstractCCIDModelBuilder<AuftragVoIPDNBuilder, AuftragVoIPDN> implements
        IServiceObject {

    private Long auftragId;
    private String sipPassword;
    private Long dnNoOrig = 1234L;
    @ReferencedEntityId("auftragId")
    private AuftragBuilder auftragBuilder;
    private Reference sipDomain;
    private List<VoipDnPlan> rufnummernplaene = Lists.newArrayList();

    @Override
    protected void beforeBuild() {
        if (sipPassword == null) {
            sipPassword = randomString(10);
        }
    }

    public AuftragVoIPDNBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public AuftragVoIPDNBuilder withDnNoOrig(Long dnNoOrig) {
        this.dnNoOrig = dnNoOrig;
        return this;
    }

    public AuftragVoIPDNBuilder withSipDomain(Reference sipDomain) {
        this.sipDomain = sipDomain;
        return this;
    }

    public AuftragVoIPDNBuilder withSIPPassword(String sipPassword) {
        this.sipPassword = sipPassword;
        return this;
    }

    public AuftragVoIPDNBuilder withRufnummernplaene(List<VoipDnPlan> voipDnPlans) {
        this.rufnummernplaene = voipDnPlans;
        return this;
    }

} // end
