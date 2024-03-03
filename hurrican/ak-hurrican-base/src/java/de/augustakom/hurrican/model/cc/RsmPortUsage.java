/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2010 12:31:47
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;


/**
 * Modell-Klasse fuer die Protokollierung des Port-Verbrauchs fuer einen bestimmten Monat fuer eine Kombination aus
 * Standort und Physiktyp(en).
 */
@Entity
@Table(name = "T_RSM_PORT_USAGE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_RSM_PORT_USAGE_0", allocationSize = 1)
public class RsmPortUsage extends AbstractCCIDModel {

    /**
     * Anzahl der Monate, die fuer die Port-Statistik beruecksichtigt werden sollen.
     */
    public static final int PORT_USAGE_MONTH_COUNT = 6;

    public static final String YEAR = "year";
    private Integer year;
    public static final String MONTH = "month";
    private Integer month;
    public static final String HVT_ID_STANDORT = "hvtIdStandort";
    private Long hvtIdStandort;
    public static final String KVZ_NUMMER = "kvzNummer";
    private String kvzNummer;
    public static final String PHYSIK_TYP_ID = "physikTypId";
    private Long physikTypId;
    public static final String PHYSIK_TYP_ID_ADD = "physikTypIdAdditional";
    private Long physikTypIdAdditional;
    public static final String PORTS_USED = "portsUsed";
    private Integer portsUsed;
    public static final String PORTS_CANCELLED = "portsCancelled";
    private Integer portsCancelled;
    public static final String DIFF_COUNT = "diffCount";
    private Integer diffCount;

    @Column(name = "YEAR")
    @NotNull
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Column(name = "MONTH")
    @NotNull
    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @Column(name = "HVT_ID_STANDORT")
    @NotNull
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    @Column(name = "KVZ_NUMMER")
    public String getKvzNummer() {
        return kvzNummer;
    }

    public void setKvzNummer(String kvzNummer) {
        this.kvzNummer = kvzNummer;
    }

    @Column(name = "PHYSIKTYP")
    @NotNull
    public Long getPhysikTypId() {
        return physikTypId;
    }

    public void setPhysikTypId(Long physikTypId) {
        this.physikTypId = physikTypId;
    }

    @Column(name = "PHYSIKTYP_ADDITIONAL")
    public Long getPhysikTypIdAdditional() {
        return physikTypIdAdditional;
    }

    public void setPhysikTypIdAdditional(Long physikTypIdAdditional) {
        this.physikTypIdAdditional = physikTypIdAdditional;
    }

    @Column(name = "PORTS_USED")
    public Integer getPortsUsed() {
        return portsUsed;
    }

    public void setPortsUsed(Integer portsUsed) {
        this.portsUsed = portsUsed;
    }

    @Column(name = "PORTS_CANCELLED")
    public Integer getPortsCancelled() {
        return portsCancelled;
    }

    public void setPortsCancelled(Integer portsCancelled) {
        this.portsCancelled = portsCancelled;
    }

    @Column(name = "DIFF_COUNT")
    public Integer getDiffCount() {
        return diffCount;
    }

    public void setDiffCount(Integer diffCount) {
        this.diffCount = diffCount;
    }

}


