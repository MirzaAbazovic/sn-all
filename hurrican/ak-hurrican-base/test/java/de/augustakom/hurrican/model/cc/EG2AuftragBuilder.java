/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2009 12:23:47
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.service.iface.IServiceObject;

/**
 *
 */
@SuppressWarnings("unused")
public class EG2AuftragBuilder extends AbstractCCIDModelBuilder<EG2AuftragBuilder, EG2Auftrag> implements
        IServiceObject {

    private AuftragBuilder auftragBuilder;
    private EGBuilder egBuilder;
    private String raum;
    private String etage;
    private String gebaeude;
    private Boolean deactivated = Boolean.FALSE;
    private Set<EndgeraetIp> endgeraetIps = new HashSet<>();
    private Set<Routing> routings = new HashSet<>();
    @ReferencedEntityId("endstelleId")
    @DontCreateBuilder
    private EndstelleBuilder endstelleBuilder;
    private IPAddressBuilder ipAddressRefBuilder;
    private Set<EGConfig> egConfigs = new HashSet<>();

    public EG2AuftragBuilder withEgBuilder(EGBuilder egBuilder) {
        this.egBuilder = egBuilder;
        return this;
    }

    public EG2AuftragBuilder withRaum(String raum) {
        this.raum = raum;
        return this;
    }

    public EG2AuftragBuilder withEtage(String etage) {
        this.etage = etage;
        return this;
    }

    public EG2AuftragBuilder withGebaeude(String gebaeude) {
        this.gebaeude = gebaeude;
        return this;
    }

    public EG2AuftragBuilder withEndgeraetIps(Set<EndgeraetIp> endgeraetIps) {
        this.endgeraetIps = endgeraetIps;
        return this;
    }

    public EG2AuftragBuilder withRoutings(Set<Routing> routings) {
        this.routings = routings;
        return this;
    }

    public EG2AuftragBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public EG2AuftragBuilder withEndstelleBuilder(EndstelleBuilder endstelleBuilder) {
        this.endstelleBuilder = endstelleBuilder;
        return this;
    }

    public EG2AuftragBuilder withIpAddressRefBuilder(IPAddressBuilder ipAddressRefBuilder) {
        this.ipAddressRefBuilder = ipAddressRefBuilder;
        return this;
    }

    public EG2AuftragBuilder withEgConfigs(Set<EGConfig> egConfigs) {
        this.egConfigs = egConfigs;
        return this;
    }
}
