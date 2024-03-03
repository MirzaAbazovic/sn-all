/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.04.2005 13:49:19
 */
package de.augustakom.hurrican.model.cc.query;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;


/**
 * Query-Objekt zur Suchte nach Equipments.
 *
 *
 */
public class EquipmentQuery extends AbstractHurricanQuery {

    private String rangVerteiler = null;
    private Long hvtIdStandort = null;
    private String rangSSType = null;
    private RangSchnittstelle rangSchnittstelle = null;
    private String rangLeiste1 = null;
    private EqStatus status = null;

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
     * @return Returns the rangLeiste1.
     */
    public String getRangLeiste1() {
        return rangLeiste1;
    }

    /**
     * @param rangLeiste1 The rangLeiste1 to set.
     */
    public void setRangLeiste1(String rangLeiste1) {
        this.rangLeiste1 = rangLeiste1;
    }

    /**
     * @return Returns the rangSchnittstelle.
     */
    public RangSchnittstelle getRangSchnittstelle() {
        return rangSchnittstelle;
    }

    /**
     * @param rangSchnittstelle The rangSchnittstelle to set.
     */
    public void setRangSchnittstelle(RangSchnittstelle rangSchnittstelle) {
        this.rangSchnittstelle = rangSchnittstelle;
    }

    /**
     * @return Returns the rangSSType.
     */
    public String getRangSSType() {
        return rangSSType;
    }

    /**
     * @param rangSSType The rangSSType to set.
     */
    public void setRangSSType(String rangSSType) {
        this.rangSSType = rangSSType;
    }

    /**
     * @return Returns the rangVerteiler.
     */
    public String getRangVerteiler() {
        return rangVerteiler;
    }

    /**
     * @param rangVerteiler The rangVerteiler to set.
     */
    public void setRangVerteiler(String rangVerteiler) {
        this.rangVerteiler = rangVerteiler;
    }

    /**
     * @return Returns the status.
     */
    public EqStatus getStatus() {
        return status;
    }

    /**
     * @param status The status to set.
     */
    public void setStatus(EqStatus status) {
        this.status = status;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        if (hvtIdStandort != null) {
            return false;
        }
        if (StringUtils.isNotBlank(getRangVerteiler())) {
            return false;
        }
        if (StringUtils.isNotBlank(getRangSSType())) {
            return false;
        }
        if ((getRangSchnittstelle() != null) && StringUtils.isNotBlank(getRangSchnittstelle().name())) {
            return false;
        }
        if (StringUtils.isNotBlank(getRangLeiste1())) {
            return false;
        }
        if ((getStatus() != null) && StringUtils.isNotBlank(getStatus().name())) {
            return false;
        }

        return true;
    }

}


