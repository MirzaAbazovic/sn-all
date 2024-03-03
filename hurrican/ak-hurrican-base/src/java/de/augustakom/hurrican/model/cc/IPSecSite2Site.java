/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2009 10:20:36
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Modell-Klasse fuer die Abbildung von IPSec Site-to-Site Daten.
 *
 *
 */
public class IPSecSite2Site extends AbstractCCIDModel implements CCAuftragModel {

    private Long auftragId;
    private String wanGateway;
    private String loopbackIp;
    private String loopbackIpPassive;
    private String virtualLanIp;
    private String virtualLanSubmask;
    private String virtualLan2Scramble;
    private String virtualWanIp;
    private String virtualWanSubmask;
    private String hostname;
    private String hostnamePassive;
    private String isdnDialInNumber;
    private Boolean splitTunnel;
    private String description;
    private String accessCarrier;
    private String accessBandwidth;
    private String accessType;

    private String accessAuftragNr;

    private Boolean hasPresharedKey;
    private Boolean hasCertificate;


    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public String getWanGateway() {
        return wanGateway;
    }

    public void setWanGateway(String wanGateway) {
        this.wanGateway = wanGateway;
    }

    public String getLoopbackIp() {
        return loopbackIp;
    }

    public void setLoopbackIp(String loopbackIp) {
        this.loopbackIp = loopbackIp;
    }

    public String getVirtualLanIp() {
        return virtualLanIp;
    }

    public void setVirtualLanIp(String virtualLanIp) {
        this.virtualLanIp = virtualLanIp;
    }

    public String getVirtualLanSubmask() {
        return virtualLanSubmask;
    }

    public void setVirtualLanSubmask(String virtualLanSubmask) {
        this.virtualLanSubmask = virtualLanSubmask;
    }

    public String getVirtualWanIp() {
        return virtualWanIp;
    }

    public void setVirtualWanIp(String virtualWanIp) {
        this.virtualWanIp = virtualWanIp;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getVirtualWanSubmask() {
        return virtualWanSubmask;
    }

    public void setVirtualWanSubmask(String virtualWanSubmask) {
        this.virtualWanSubmask = virtualWanSubmask;
    }

    public String getIsdnDialInNumber() {
        return isdnDialInNumber;
    }

    public void setIsdnDialInNumber(String isdnDialInNumber) {
        this.isdnDialInNumber = isdnDialInNumber;
    }

    public Boolean getSplitTunnel() {
        return splitTunnel;
    }

    public void setSplitTunnel(Boolean splitTunnel) {
        this.splitTunnel = splitTunnel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccessCarrier() {
        return accessCarrier;
    }

    public void setAccessCarrier(String accessCarrier) {
        this.accessCarrier = accessCarrier;
    }

    public String getAccessBandwidth() {
        return accessBandwidth;
    }

    public void setAccessBandwidth(String accessBandwidth) {
        this.accessBandwidth = accessBandwidth;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getAccessAuftragNr() {
        return accessAuftragNr;
    }

    public void setAccessAuftragNr(String accessAuftragNr) {
        this.accessAuftragNr = accessAuftragNr;
    }

    public Boolean getHasPresharedKey() {
        return hasPresharedKey;
    }

    public void setHasPresharedKey(Boolean hasPresharedKey) {
        this.hasPresharedKey = hasPresharedKey;
    }

    public Boolean getHasCertificate() {
        return hasCertificate;
    }

    public void setHasCertificate(Boolean hasCertificate) {
        this.hasCertificate = hasCertificate;
    }

    public String getLoopbackIpPassive() {
        return loopbackIpPassive;
    }

    public void setLoopbackIpPassive(String loopbackIpPassive) {
        this.loopbackIpPassive = loopbackIpPassive;
    }

    public String getHostnamePassive() {
        return hostnamePassive;
    }

    public void setHostnamePassive(String hostnamePassive) {
        this.hostnamePassive = hostnamePassive;
    }

    public String getVirtualLan2Scramble() {
        return virtualLan2Scramble;
    }

    public void setVirtualLan2Scramble(String virtualLan2Scramble) {
        this.virtualLan2Scramble = virtualLan2Scramble;
    }

}


