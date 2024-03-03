/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.10.2009 17:38:21
 */
package de.augustakom.hurrican.model.cc;


/**
 * BRAS Pools sind benamte Min/Max-Bereiche fuer Wert des Virtual Channel eines Virtual Path.
 * <p/>
 * BRAS Pools werden momentan nur manuell in der Datenbank gepflegt.
 * <p/>
 * Aktuell nutzen VPNs diese Bereiche, wobei 90 als VP fuer klein-VPNs genutzt wird, als auch ATM SDSL-Ports, die die
 * VPs 8 und 10 nutzen.
 */
public class BrasPool extends AbstractCCIDModel {
    public static final String ATM_SDSL_POOL_PREFIX = "ATM SDSL Pool";

    private String name;
    public static final String NAME = "name";
    private Integer vp;
    public static final String VP = "vp";
    private Integer vcMin;
    public static final String VC_MIN = "vcMin";
    private Integer vcMax;
    public static final String VC_MAX = "vcMax";
    private String nasIdentifier;
    private Integer port;
    private Integer slot;
    private String backupNasIdentifier;
    private Integer backupPort;
    private Integer backupSlot;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVp() {
        return vp;
    }

    public void setVp(Integer vp) {
        this.vp = vp;
    }

    public Integer getVcMin() {
        return vcMin;
    }

    public void setVcMin(Integer vcMin) {
        this.vcMin = vcMin;
    }

    public Integer getVcMax() {
        return vcMax;
    }

    public void setVcMax(Integer vcMax) {
        this.vcMax = vcMax;
    }

    public String getNasIdentifier() {
        return nasIdentifier;
    }

    public void setNasIdentifier(String nasIdentifier) {
        this.nasIdentifier = nasIdentifier;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getSlot() {
        return slot;
    }

    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    public String getBackupNasIdentifier() {
        return backupNasIdentifier;
    }

    public void setBackupNasIdentifier(String backupNasIdentifier) {
        this.backupNasIdentifier = backupNasIdentifier;
    }

    public Integer getBackupPort() {
        return backupPort;
    }

    public void setBackupPort(Integer backupPort) {
        this.backupPort = backupPort;
    }

    public Integer getBackupSlot() {
        return backupSlot;
    }

    public void setBackupSlot(Integer backupSlot) {
        this.backupSlot = backupSlot;
    }
}
