/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.10.2008 15:56:06
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

/**
 * Modell fuer ein Objekt vom Typ RS_Monitor_Config
 */
@Entity
@Table(name = "T_RS_MONITOR_CONFIG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_RS_MONITOR_CONFIG_0", allocationSize = 1)
public class RSMonitorConfig extends AbstractCCIDModel {

    // Konstante falls der zu pruefenden Wert groesser ist als der Schwellwert
    public static final int SCHWELLWERT_OK = 0;
    // Konstante falls der zu pruefenden Wert kleiner ist als der Schwellwert
    public static final int SCHWELLWERT_UNTERSCHRITTEN = 1;
    // Konstante falls der zu pruefenden Wert max. 25 Prozent ueber oder gleich dem Schwellwert ist
    public static final int SCHWELLWERT_FAST_UNTERSCHRITTEN = 2;
    // Konstante falls kein Schwellwert definiert ist
    public static final int SCHWELLWERT_NICHT_DEFINIERT = 3;

    private Long hvtIdStandort = null;
    private String kvzNummer = null;
    private Long monitorType = null;
    private Long physiktyp = null;
    private Long physiktypAdd = null;
    private String eqRangSchnittstelle = null;
    private String eqUEVT = null;
    private Integer minCount = null;
    private String userw = null;
    private Date datew = null;
    private Boolean alarmierung = null;
    private Integer dayCount = null;

    @Column(name = "EQ_RANG_SCHNITTSTELLE")
    public String getEqRangSchnittstelle() {
        return eqRangSchnittstelle;
    }

    public void setEqRangSchnittstelle(String eqRangSchnittstelle) {
        this.eqRangSchnittstelle = eqRangSchnittstelle;
    }

    @Column(name = "EQ_UEVT")
    public String getEqUEVT() {
        return eqUEVT;
    }

    public void setEqUEVT(String eqUEVT) {
        this.eqUEVT = eqUEVT;
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

    @Column(name = "MIN_COUNT")
    public Integer getMinCount() {
        return minCount;
    }

    public void setMinCount(Integer minCount) {
        this.minCount = minCount;
    }

    @Column(name = "MONITOR_TYPE")
    @NotNull
    public Long getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(Long monitorType) {
        this.monitorType = monitorType;
    }

    @Column(name = "PHYSIKTYP")
    public Long getPhysiktyp() {
        return physiktyp;
    }

    public void setPhysiktyp(Long physiktyp) {
        this.physiktyp = physiktyp;
    }

    @Column(name = "PHYSIKTYP_ADD")
    public Long getPhysiktypAdd() {
        return physiktypAdd;
    }

    public void setPhysiktypAdd(Long physiktypAdd) {
        this.physiktypAdd = physiktypAdd;
    }

    @Column(name = "ALARMIERUNG")
    @NotNull
    public Boolean getAlarmierung() {
        return alarmierung;
    }

    public void setAlarmierung(Boolean alarmierung) {
        this.alarmierung = alarmierung;
    }

    @Column(name = "DAY_COUNT")
    public Integer getDayCount() {
        return dayCount;
    }

    public void setDayCount(Integer dayCount) {
        this.dayCount = dayCount;
    }

    @Column(name = "USERW")
    public String getUserw() {
        return userw;
    }

    public void setUserw(String userw) {
        this.userw = userw;
    }

    @Column(name = "DATEW")
    @Type(type = "timestamp")
    public Date getDatew() {
        return datew;
    }

    public void setDatew(Date datew) {
        this.datew = datew;
    }

}
