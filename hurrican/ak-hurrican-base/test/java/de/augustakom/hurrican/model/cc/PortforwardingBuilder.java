/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.09.2011 17:30:12
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.hurrican.model.cc.PortForwarding.TransportProtocolType;

/**
 *
 */
public class PortforwardingBuilder extends AbstractCCIDModelBuilder<PortforwardingBuilder, PortForwarding> implements
        IServiceObject {

    private IPAddress sourceIpAddressRef = null;
    private Integer sourcePort = 80;
    private IPAddress destIpAddressRef = null;
    private Integer destPort = 22;
    private String transportProtocol = TransportProtocolType.TCP.name();
    private String bemerkung = "Bemerkung";
    private Boolean active = Boolean.TRUE;
    private String bearbeiter = "Testbearbeiter";

    public PortforwardingBuilder withSourceAddress(IPAddressBuilder ipAddressBuilder) {
        this.sourceIpAddressRef = ipAddressBuilder.build();
        return this;
    }

    public PortforwardingBuilder withSourcePort(Integer sourcePort) {
        this.sourcePort = sourcePort;
        return this;
    }

    public PortforwardingBuilder withDestinationAddress(IPAddressBuilder ipAddressBuilder) {
        this.destIpAddressRef = ipAddressBuilder.build();
        return this;
    }

    public PortforwardingBuilder withDestinationPort(Integer destinationPort) {
        this.destPort = destinationPort;
        return this;
    }

    public PortforwardingBuilder withBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
        return this;
    }

    public PortforwardingBuilder withTransportProtocolType(TransportProtocolType type) {
        this.transportProtocol = type.name();
        return this;
    }

    public PortforwardingBuilder withBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
        return this;
    }

    public PortforwardingBuilder active(Boolean active) {
        this.active = active;
        return this;
    }

} // end
