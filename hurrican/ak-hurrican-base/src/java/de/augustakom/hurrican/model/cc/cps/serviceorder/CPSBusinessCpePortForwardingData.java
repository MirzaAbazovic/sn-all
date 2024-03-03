/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2010 07:55:37
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.PortForwarding;


/**
 * Modell-Klasse fuer die Abbildung von Port-Forwardings fuer ein Business CPE.
 */
public class CPSBusinessCpePortForwardingData extends AbstractCPSServiceOrderDataModel {

    private String protocol;
    private String sourceIpV4;
    private String destinationIpV4;
    private String sourceIpV6;
    private String destinationIpV6;
    private Integer sourcePort;
    private Integer destinationPort;


    /**
     * Konstruktor mit Angabe des {@link PortForwarding} Objekts, aus dem das CPS Modell aufgebaut werden soll.
     *
     * @param portForwarding
     */
    public CPSBusinessCpePortForwardingData(PortForwarding portForwarding) {
        setProtocol(portForwarding.getTransportProtocol());
        setSourcePort(portForwarding.getSourcePort());
        setDestinationPort(portForwarding.getDestPort());

        if (portForwarding.getSourceIpAddressRef() != null) {
            if (portForwarding.getSourceIpAddressRef().isIPV4()) {
                setSourceIpV4(getAbsoluteAddressOrDefault(portForwarding.getSourceIpAddressRef()));
            }
            else if (portForwarding.getSourceIpAddressRef().isIPV6()) {
                setSourceIpV6(getAbsoluteAddressOrDefault(portForwarding.getSourceIpAddressRef()));
            }
        }
        if (portForwarding.getDestIpAddressRef() != null) {
            if (portForwarding.getDestIpAddressRef().isIPV4()) {
                setDestinationIpV4(getAbsoluteAddressOrDefault(portForwarding.getDestIpAddressRef()));
            }
            else if (portForwarding.getDestIpAddressRef().isIPV6()) {
                setDestinationIpV6(getAbsoluteAddressOrDefault(portForwarding.getDestIpAddressRef()));
            }
        }
    }

    private String getAbsoluteAddressOrDefault(IPAddress ipAddress) {
        String address = ipAddress.getAbsoluteAddress();
        if (address == null) {
            address = ipAddress.getAddress();
        }
        return address;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getSourceIpV4() {
        return sourceIpV4;
    }

    public void setSourceIpV4(String sourceIpV4) {
        this.sourceIpV4 = sourceIpV4;
    }

    public String getDestinationIpV4() {
        return destinationIpV4;
    }

    public void setDestinationIpV4(String destinationIpV4) {
        this.destinationIpV4 = destinationIpV4;
    }

    public String getSourceIpV6() {
        return sourceIpV6;
    }

    public void setSourceIpV6(String sourceIpV6) {
        this.sourceIpV6 = sourceIpV6;
    }

    public String getDestinationIpV6() {
        return destinationIpV6;
    }

    public void setDestinationIpV6(String destinationIpV6) {
        this.destinationIpV6 = destinationIpV6;
    }

    public Integer getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(Integer sourcePort) {
        this.sourcePort = sourcePort;
    }

    public Integer getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(Integer destinationPort) {
        this.destinationPort = destinationPort;
    }

}


