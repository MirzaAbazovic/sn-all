/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2009 12:23:47
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.hurrican.dao.cc.VrrpPriority;


/**
 * Builder fuer EG-Config-Objekte.
 */
@SuppressWarnings("unused")
public class EGConfigBuilder extends AbstractCCIDModelBuilder<EGConfigBuilder, EGConfig> implements
        IServiceObject {

    @ReferencedEntityId("egType")
    private EGTypeBuilder egTypeBuilder;
    private String serialNumber;
    private String egUser;
    private String egPassword;
    private String modellZusatz;
    private EG2AuftragBuilder eg2AuftragBuilder;
    private Set<PortForwarding> portForwardings = new HashSet<>();
    private Set<EndgeraetAcl> endgeraetAcls = new HashSet<>();
    private Boolean natActive = Boolean.FALSE;
    private Boolean dhcpActive = Boolean.FALSE;
    private Boolean wanIpFest = Boolean.FALSE;
    private Schicht2Protokoll schicht2Protokoll;
    private ServiceVertrag serviceVertrag;
    private String softwarestand;
    private VrrpPriority vrrpPriority;

    public EGConfigBuilder withSoftwarestand(final String swStand) {
        this.softwarestand = swStand;
        return this;
    }

    public EGConfigBuilder withModellZusatz(String modellzusatz) {
        this.modellZusatz = modellzusatz;
        return this;
    }

    public EGConfigBuilder withEGTypeBuilder(EGTypeBuilder egTypeBuilder) {
        this.egTypeBuilder = egTypeBuilder;
        return this;
    }

    public EGConfigBuilder withSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public EGConfigBuilder withEG2AuftragBuilder(EG2AuftragBuilder eg2AuftragBuilder) {
        this.eg2AuftragBuilder = eg2AuftragBuilder;
        return this;
    }

    public EGConfigBuilder withPortForwardings(Set<PortForwarding> portForwardings) {
        this.portForwardings = portForwardings;
        return this;
    }

    public EGConfigBuilder withEndgeraetAcls(Set<EndgeraetAcl> endgeraetAcls) {
        this.endgeraetAcls = endgeraetAcls;
        return this;
    }

    public EGConfigBuilder withEndgeraetAcl(EndgeraetAcl endgeraetAcl) {
        this.endgeraetAcls.add(endgeraetAcl);
        return this;
    }

    public EGConfigBuilder withNatActive(Boolean natActive) {
        this.natActive = natActive;
        return this;
    }

    public EGConfigBuilder withDhcpActive(Boolean dhcpActive) {
        this.dhcpActive = dhcpActive;
        return this;
    }

    public EGConfigBuilder withSchicht2Protokoll(Schicht2Protokoll schicht2Protokoll) {
        this.schicht2Protokoll = schicht2Protokoll;
        return this;
    }

    public EGConfigBuilder withServiceVertrag(ServiceVertrag serviceVertrag) {
        this.serviceVertrag = serviceVertrag;
        return this;
    }

    public EGConfigBuilder withEgUser(String egUser) {
        this.egUser = egUser;
        return this;
    }

    public EGConfigBuilder withEgPassword(String egPassword) {
        this.egPassword = egPassword;
        return this;
    }

    public EGConfigBuilder withWanIpFest(Boolean wanIpFest) {
        this.wanIpFest = wanIpFest;
        return this;
    }

    public EGConfigBuilder withVrrpPriority(VrrpPriority vrrpPriority) {
        this.vrrpPriority = vrrpPriority;
        return this;
    }
}
