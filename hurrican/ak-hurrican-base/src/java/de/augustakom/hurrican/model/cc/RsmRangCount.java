/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2008 11:56:06
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.common.tools.lang.NumberTools;


/**
 * Modell fuer ein Objekt vom Typ RSM_RANG_COUNT
 */
@Entity
@Table(name = "T_RSM_RANG_COUNT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_RSM_RANG_COUNT_0", allocationSize = 1)
public class RsmRangCount extends AbstractCCIDModel {

    private Long hvtStandortId = null;
    private String kvzNummer = null;
    private Long physiktyp = null;
    private Long physiktypAdd = null;
    private Integer belegt = null;
    private Integer frei = null;
    private Integer freigabebereit = null;
    private Integer defekt = null;
    private Integer imAufbau = null;
    private Integer vorhanden = null;
    private Integer portReach = null;
    private Float averageUsage = null;

    /**
     * Copy Constructor
     */
    public RsmRangCount(RsmRangCount rsmRangCount) {
        hvtStandortId = rsmRangCount.hvtStandortId;
        kvzNummer = rsmRangCount.kvzNummer;
        physiktyp = rsmRangCount.physiktyp;
        physiktypAdd = rsmRangCount.physiktypAdd;
        belegt = rsmRangCount.belegt;
        frei = rsmRangCount.frei;
        freigabebereit = rsmRangCount.freigabebereit;
        defekt = rsmRangCount.defekt;
        imAufbau = rsmRangCount.imAufbau;
        vorhanden = rsmRangCount.vorhanden;
        portReach = rsmRangCount.portReach;
        averageUsage = rsmRangCount.averageUsage;
    }


    /**
     * Default Constructor
     *
     *
     */
    public RsmRangCount() {
    }


    /**
     * Setzt alle Counter des {@link RsmRangCount} Objekts auf 0 zurueck.
     */
    public void initCounter() {
        setFrei(Integer.valueOf(0));
        setFreigabebereit(Integer.valueOf(0));
        setBelegt(Integer.valueOf(0));
        setDefekt(Integer.valueOf(0));
        setImAufbau(Integer.valueOf(0));
        setVorhanden(Integer.valueOf(0));
    }

    /**
     * Gibt die Summe der Ports 'frei' und 'im Aufbau' zurueck.
     *
     * @return Summe der freien bzw. im Aufbau befindlicher Ports
     *
     */
    @Transient
    public Integer getPortCountFree() {
        return NumberTools.add(getFrei(), getImAufbau());
    }

    @Column(name = "BELEGT")
    @NotNull
    public Integer getBelegt() {
        return belegt;
    }

    public void setBelegt(Integer belegt) {
        this.belegt = belegt;
    }

    public void incBelegt() {
        this.belegt = NumberTools.add(belegt, Integer.valueOf(1));
    }

    @Column(name = "FREI")
    @NotNull
    public Integer getFrei() {
        return frei;
    }

    public void setFrei(Integer frei) {
        this.frei = frei;
    }

    public void incFrei() {
        this.frei = NumberTools.add(frei, Integer.valueOf(1));
    }

    @Column(name = "FREIGABE_BEREIT")
    @NotNull
    public Integer getFreigabebereit() {
        return freigabebereit;
    }

    public void setFreigabebereit(Integer freigabebereit) {
        this.freigabebereit = freigabebereit;
    }

    public void incFreigabebereit() {
        this.freigabebereit = NumberTools.add(freigabebereit, Integer.valueOf(1));
    }

    @Column(name = "HVT_ID_STANDORT")
    @NotNull
    public Long getHvtStandortId() {
        return hvtStandortId;
    }

    public void setHvtStandortId(Long hvtStandortId) {
        this.hvtStandortId = hvtStandortId;
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
    public Long getPhysiktyp() {
        return physiktyp;
    }

    public void setPhysiktyp(Long physiktyp) {
        this.physiktyp = physiktyp;
    }

    @Column(name = "PHYSIKTYP_ADDITIONAL")
    public Long getPhysiktypAdd() {
        return physiktypAdd;
    }

    public void setPhysiktypAdd(Long physiktypAdd) {
        this.physiktypAdd = physiktypAdd;
    }

    @Column(name = "VORHANDEN")
    @NotNull
    public Integer getVorhanden() {
        return vorhanden;
    }

    public void setVorhanden(Integer vorhanden) {
        this.vorhanden = vorhanden;
    }

    public void incVorhanden() {
        this.vorhanden = NumberTools.add(vorhanden, Integer.valueOf(1));
    }

    @Column(name = "DEFEKT")
    @NotNull
    public Integer getDefekt() {
        return defekt;
    }

    public void setDefekt(Integer defekt) {
        this.defekt = defekt;
    }

    public void incDefekt() {
        this.defekt = NumberTools.add(defekt, Integer.valueOf(1));
    }

    @Column(name = "IM_AUFBAU")
    @NotNull
    public Integer getImAufbau() {
        return imAufbau;
    }

    public void setImAufbau(Integer imAufbau) {
        this.imAufbau = imAufbau;
    }

    public void incImAufbau() {
        this.imAufbau = NumberTools.add(imAufbau, Integer.valueOf(1));
    }

    @Column(name = "PORT_REACH")
    public Integer getPortReach() {
        return portReach;
    }

    public void setPortReach(Integer portReach) {
        this.portReach = portReach;
    }

    @Column(name = "AVERAGE_USAGE")
    public Float getAverageUsage() {
        return averageUsage;
    }

    public void setAverageUsage(Float averageUsage) {
        this.averageUsage = averageUsage;
    }

    /**
     * Ueberprueft, ob der Schwellwert fuer die freien Ports unterschritten ist.
     *
     * @param threshold zu pruefender Schwellwert
     * @return Konstante aus RSMonitorConfig die angibt, ob der Schwellwert unterschritten, fast erreich oder noch i.O.
     * ist
     */
    public int checkFreePortThreshold(Integer threshold) {
        // Zu pruefenden Wert ermitteln
        Integer toCheck = getPortCountFree();
        return checkThreshold(toCheck, threshold);
    }

    /**
     * Ueberprueft, ob der Schwellwert fuer die Port-Reichweite erreicht ist.
     *
     * @param threshold
     * @return Konstante aus RSMonitorConfig die angibt, ob der Schwellwert unterschritten, fast erreich oder noch i.O.
     * ist
     */
    public int checkPortUsageThreshold(Integer threshold) {
        Integer toCheck = getPortReach();
        if (NumberTools.isLess(toCheck, Integer.valueOf(0))) {
            return RSMonitorConfig.SCHWELLWERT_OK;
        }
        return checkThreshold(toCheck, threshold);
    }

    /**
     * Ueberprueft, ob der zu pruefende Wert ({@code toCheck}) den angegebenen Schwellwert ({@code threshold}) erreicht
     * hat.
     *
     * @param threshold zu pruefender Schwellwert
     * @param toCheck   zu pruefender Wert
     * @param threshold Schwellwert
     * @return Konstante aus RSMonitorConfig die angibt, ob der Schwellwert unterschritten, fast erreich oder noch i.O.
     * ist
     */
    public int checkThreshold(Integer toCheck, Integer threshold) {
        // Kein Schwellwert definiert
        if (threshold == null) {
            return RSMonitorConfig.SCHWELLWERT_NICHT_DEFINIERT;
        }
        // Schwellwert unterschritten
        else if (NumberTools.isLess(toCheck, threshold)) {
            return RSMonitorConfig.SCHWELLWERT_UNTERSCHRITTEN;
        }
        // Schwellwert fast unterschritten
        else if (NumberTools.isLess(toCheck, threshold.doubleValue() * 1.25d)) {
            return RSMonitorConfig.SCHWELLWERT_FAST_UNTERSCHRITTEN;
        }
        else {
            return RSMonitorConfig.SCHWELLWERT_OK;
        }
    }

}
