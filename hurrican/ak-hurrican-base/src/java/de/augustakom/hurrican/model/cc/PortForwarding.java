/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2006 10:46:10
 */
package de.augustakom.hurrican.model.cc;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;


/**
 * Entitaet zur Abbildung eines Port-Forwardings.
 *
 *
 *
 */
public class PortForwarding extends AbstractCCIDModel {

    public static final PortForwardingComparator PORTFORWARDING_COMPARATOR = new PortForwardingComparator();

    public static final class PortForwardingComparator implements Comparator<PortForwarding>, Serializable {
        @Override
        public int compare(PortForwarding o1, PortForwarding o2) {
            if (!o1.getDestIpAddressRef().getAddress().equals(o2.getDestIpAddressRef().getAddress())) {
                return o1.getDestIpAddressRef().getAddress().compareTo(o2.getDestIpAddressRef().getAddress());
            }
            if (!o1.getDestPort().equals(o2.getDestPort())) {
                return o1.getDestPort().compareTo(o2.getDestPort());
            }

            if ((o1.getSourceIpAddressRef() != null) && (o2.getSourceIpAddressRef() != null)
                    && !o1.getSourceIpAddressRef().getAddress().equals(o2.getSourceIpAddressRef().getAddress())) {
                return o1.getSourceIpAddressRef().getAddress().compareTo(o2.getSourceIpAddressRef().getAddress());
            }
            if ((o1.getSourcePort() != null)
                    && (o2.getSourcePort() != null)
                    && !o1.getSourcePort().equals(o2.getSourcePort())) {
                return o1.getSourcePort().compareTo(o2.getSourcePort());
            }
            return 0;
        }
    }

    /**
     * Moegliche Transport-Protokolle
     */
    public enum TransportProtocolType {
        UDP, TCP, ALL
    }

    /**
     * Kommentarfeld fuer ein Port-Forwarding
     */
    private String bemerkung;

    /**
     * Ist das Port-Forwarding aktiv
     */
    private Boolean active;

    /**
     * Letzter Bearbeiter des Port-Forwardings
     */
    private String bearbeiter;

    /**
     * Zugrunde liegendes Transport-Protokoll
     */
    private String transportProtocol;

    /**
     * Quell-Port A value of -1 means all ports.
     */
    private Integer sourcePort;

    /**
     * Ziel-Port A value of -1 means all ports.
     */
    private Integer destPort;

    /**
     * Ziel-IP-Adresse (IPAddress Reference)
     */
    private IPAddress destIpAddressRef;

    /**
     * Quell-IP-Adresse (IPAddress Reference)
     */
    private IPAddress sourceIpAddressRef;


    public String getBearbeiter() {
        return this.bearbeiter;
    }

    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    public String getBemerkung() {
        return this.bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Boolean getActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getTransportProtocol() {
        return transportProtocol;
    }

    public String getTransportProtocolNullsafe() {
        if (transportProtocol != null) {
            return transportProtocol;
        }
        return "";
    }

    public void setTransportProtocol(String transportProtocol) {
        if (transportProtocol != null) {
            TransportProtocolType.valueOf(transportProtocol.toUpperCase());
            this.transportProtocol = transportProtocol;
        }
        else {
            this.transportProtocol = null;
        }
    }

    public void setTransportProtocol(TransportProtocolType transportProtocol) {
        if (transportProtocol != null) {
            this.transportProtocol = transportProtocol.name();
        }
        else {
            this.transportProtocol = null;
        }
    }

    public Integer getDestPort() {
        return destPort;
    }

    public void setDestPort(Integer destPort) {
        this.destPort = destPort;
    }

    public void setSourcePort(Integer sourcePort) {
        this.sourcePort = sourcePort;
    }

    public Integer getSourcePort() {
        return sourcePort;
    }

    public String getSourcePortNullsafe() {
        if (sourcePort != null) {
            return sourcePort.toString();
        }
        return "";
    }

    public IPAddress getDestIpAddressRef() {
        return destIpAddressRef;
    }

    public void setDestIpAddressRef(IPAddress destIpAddressRef) {
        this.destIpAddressRef = destIpAddressRef;
    }

    public IPAddress getSourceIpAddressRef() {
        return sourceIpAddressRef;
    }

    public String getSourceIpAddressNullSafe() {
        if ((getSourceIpAddressRef() != null)
                && StringUtils.isNotBlank(getSourceIpAddressRef().getAddress())) {
            return getSourceIpAddressRef().getAddress();
        }
        return "";
    }

    public void setSourceIpAddressRef(IPAddress sourceIpAddressRef) {
        this.sourceIpAddressRef = sourceIpAddressRef;
    }

}


