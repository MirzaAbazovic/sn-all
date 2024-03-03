/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.05.2005 16:23:28
 */
package de.augustakom.hurrican.model.shared.view;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;


/**
 * Query-Objekt fuer die Suche nach bzw. ueber Equipment-Daten.
 *
 *
 */
public class AuftragEquipmentQuery extends AbstractHurricanQuery {

    private Long hvtIdStandort = null;
    private String eqBucht = null;
    private String eqLeiste1 = null;
    private String eqStift1 = null;
    private String eqSwitch = null;
    private String eqHwEqn = null;
    private String eqGeraetebezeichnung = null;
    private String eqMgmtbezeichnung = null;
    private boolean onlyActive = false;

    /**
     * @return Returns the eqBucht.
     */
    public String getEqBucht() {
        return eqBucht;
    }

    /**
     * @param eqBucht The eqBucht to set.
     */
    public void setEqBucht(String eqBucht) {
        this.eqBucht = eqBucht;
    }

    /**
     * @return Returns the eqHwEqn.
     */
    public String getEqHwEqn() {
        return eqHwEqn;
    }

    /**
     * @param eqHwEqn The eqHwEqn to set.
     */
    public void setEqHwEqn(String eqHwEqn) {
        this.eqHwEqn = eqHwEqn;
    }

    /**
     * @return Returns the eqLeiste1.
     */
    public String getEqLeiste1() {
        return eqLeiste1;
    }

    /**
     * @param eqLeiste1 The eqLeiste1 to set.
     */
    public void setEqLeiste1(String eqLeiste1) {
        this.eqLeiste1 = eqLeiste1;
    }

    /**
     * @return Returns the eqStift1.
     */
    public String getEqStift1() {
        return eqStift1;
    }

    /**
     * @param eqStift1 The eqStift1 to set.
     */
    public void setEqStift1(String eqStift1) {
        this.eqStift1 = eqStift1;
    }

    /**
     * @return Returns the eqSwitch.
     */
    public String getEqSwitch() {
        return eqSwitch;
    }

    /**
     * @param eqSwitch The eqSwitch to set.
     */
    public void setEqSwitch(String eqSwitch) {
        this.eqSwitch = eqSwitch;
    }

    /**
     * @return Returns the hvtIdStandort.
     */
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    /**
     * @param hvtIdStandort The hvtIdStandort to set.
     */
    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    /**
     * @return Returns the onlyActive.
     */
    public boolean isOnlyActive() {
        return onlyActive;
    }

    /**
     * @param onlyActive The onlyActive to set.
     */
    public void setOnlyActive(boolean onlyActive) {
        this.onlyActive = onlyActive;
    }

    public String getEqGeraetebezeichnung() {
        return eqGeraetebezeichnung;
    }

    public void setEqGeraetebezeichnung(String eqGeraetebezeichnung) {
        this.eqGeraetebezeichnung = eqGeraetebezeichnung;
    }

    public String getEqMgmtbezeichnung() {
        return eqMgmtbezeichnung;
    }

    public void setEqMgmtbezeichnung(String eqMgmtbezeichnung) {
        this.eqMgmtbezeichnung = eqMgmtbezeichnung;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        if (hvtIdStandort != null) {
            return false;
        }
        if (StringUtils.isNotBlank(eqBucht)) {
            return false;
        }
        if (StringUtils.isNotBlank(eqLeiste1)) {
            return false;
        }
        if (StringUtils.isNotBlank(eqStift1)) {
            return false;
        }
        if (StringUtils.isNotBlank(eqSwitch)) {
            return false;
        }
        if (StringUtils.isNotBlank(eqHwEqn)) {
            return false;
        }
        if (StringUtils.isNotBlank(eqGeraetebezeichnung)) {
            return false;
        }
        if (StringUtils.isNotBlank(eqMgmtbezeichnung)) {
            return false;
        }
        if (onlyActive) {
            return false;
        }
        return true;
    }

    /**
     * Gibt an, ob genügend Suchparameter angegeben wurden um ein zu großes Suchergebnis zu vermeiden (und damit eine
     * OOM).
     *
     * @return
     */
    public boolean isIncomplete() {
        if (hvtIdStandort != null) {
            return false;
        }
        if (StringUtils.isNotBlank(eqBucht)) {
            return false;
        }
        if (StringUtils.isNotBlank(eqLeiste1)) {
            return false;
        }
        if (StringUtils.isNotBlank(eqStift1)) {
            return false;
        }
        if (StringUtils.isNotBlank(eqHwEqn)) {
            return false;
        }
        if (StringUtils.isNotBlank(eqGeraetebezeichnung)) {
            return false;
        }
        if (StringUtils.isNotBlank(eqMgmtbezeichnung)) {
            return false;
        }
        return true;
    }

}


