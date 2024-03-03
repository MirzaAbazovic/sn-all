/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.03.2005 14:57:18
 */
package de.augustakom.hurrican.model.cc.view;

import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.shared.iface.HvtIdStandortModel;


/**
 * View-Modell, um die Anzahl der verfuegbaren und die Anzahl der bereits rangierten Stifte zu einem UEVT anzuzeigen.
 *
 *
 */
public class EquipmentBelegungView extends AbstractCCModel implements HvtIdStandortModel, DebugModel {

    private Long hvtIdStandort = null;
    private String switchAK = null;
    private String uevt = null;
    private String leiste1 = null;
    private Integer stifteGesamt = null;
    private String stiftMin = null;
    private String stiftMax = null;
    private String physiktyp = null;
    private Integer stifteRangiert = null;

    public String getLeiste1() {
        return leiste1;
    }

    public void setLeiste1(String leiste1) {
        this.leiste1 = leiste1;
    }

    public String getPhysiktyp() {
        return physiktyp;
    }

    public void setPhysiktyp(String physiktyp) {
        this.physiktyp = physiktyp;
    }

    /**
     * Gibt die Anzahl der Stifte zurueck, die bereits rangiert sind.
     *
     * @return Returns the stifteRangiert.
     */
    public Integer getStifteRangiert() {
        return stifteRangiert;
    }

    public void setStifteRangiert(Integer stifteRangiert) {
        this.stifteRangiert = stifteRangiert;
    }

    /**
     * Gibt die Anzahl der Stifte zurueck, die fuer auf dieser Leiste eingespielt sind.
     *
     * @return Returns the stifteGesamt.
     */
    public Integer getStifteGesamt() {
        return stifteGesamt;
    }

    public void setStifteGesamt(Integer stifteGesamt) {
        this.stifteGesamt = stifteGesamt;
    }

    public String getStiftMax() {
        return stiftMax;
    }

    public void setStiftMax(String stiftMax) {
        this.stiftMax = stiftMax;
    }

    public String getStiftMin() {
        return stiftMin;
    }

    public void setStiftMin(String stiftMin) {
        this.stiftMin = stiftMin;
    }

    public String getUevt() {
        return uevt;
    }

    public void setUevt(String uevt) {
        this.uevt = uevt;
    }

    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    public String getSwitchAK() {
        return switchAK;
    }

    public void setSwitchAK(String switchAK) {
        this.switchAK = switchAK;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + this.getClass().getName());
            logger.debug("  HVT-Standort-ID: " + getHvtIdStandort());
            logger.debug("  Switch         : " + getSwitchAK());
            logger.debug("  UEVT           : " + getUevt());
            logger.debug("  Leiste         : " + getLeiste1());
            logger.debug("  Stifte Gesamt  : " + getStifteGesamt());
            logger.debug("  Stifte rangiert: " + getStifteRangiert());
            logger.debug("  Stift min      : " + getStiftMin());
            logger.debug("  Stift max      : " + getStiftMax());
            logger.debug("  Physiktyp      : " + getPhysiktyp());
        }
    }

}


