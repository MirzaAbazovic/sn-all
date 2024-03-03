/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.01.2007 13:41:20
 */
package de.augustakom.hurrican.model.cc.innenauftrag;

import java.util.*;

import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell fuer die Darstellung einer Materialentnahme. <br> Eine Materialentnahme ist immer einem Budget zugeordnet.
 * <br> An der Materialentnahme wiederum koennen einzelne Artikil 'haengen'. Eine Materialentnahme ist ausserdem immer
 * fuer ein bestimmtes Lager gedacht.
 *
 *
 */
public class IAMaterialEntnahme extends AbstractCCIDModel {

    /**
     * Konstante fuer <code>entnahmetyp</code> definiert eine Reservierung.
     */
    public static final Short TYP_RESERVATION = Short.valueOf((short) 1);
    /**
     * Konstante fuer <code>entnahmetyp</code> definiert eine Entnahme.
     */
    public static final Short TYP_WITHDRAWL = Short.valueOf((short) 2);

    private static final String TYP_TEXT_RESERVATION = "Reservierung";
    private static final String TYP_TEXT_WITHDRAWL = "Entnahme";

    public static final String IA_BUDGET_ID = "iaBudgetId";
    private Long iaBudgetId = null;
    public static final String ENTNAHME_TYP = "entnahmetyp";
    public static final String ENTNAHME_TYP_TEXT = "entnahmetypText";
    private Short entnahmetyp = null;
    public static final String LAGER_ID = "lagerId";
    private Long lagerId = null;
    public static final String CREATED_AT = "createdAt";
    private Date createdAt = null;
    public static final String CREATED_FROM = "createdFrom";
    private String createdFrom = null;
    public static final String HVT_ID_STANDORT = "hvtIdStandort";
    private Long hvtIdStandort = null;

    /**
     * @return Returns the entnahmetyp.
     */
    public Short getEntnahmetyp() {
        return this.entnahmetyp;
    }

    /**
     * @param entnahmetyp The entnahmetyp to set.
     */
    public void setEntnahmetyp(Short entnahmetyp) {
        this.entnahmetyp = entnahmetyp;
    }

    /**
     * Gibt einen Namen/Text fuer den aktuellen Entnahmetyp zurueck.
     *
     * @return
     *
     */
    public String getEntnahmetypText() {
        if (TYP_RESERVATION.equals(getEntnahmetyp())) {
            return TYP_TEXT_RESERVATION;
        }
        else if (TYP_WITHDRAWL.equals(getEntnahmetyp())) {
            return TYP_TEXT_WITHDRAWL;
        }
        return HurricanConstants.UNKNOWN;
    }

    /**
     * @return Returns the iaBudgetId.
     */
    public Long getIaBudgetId() {
        return this.iaBudgetId;
    }

    /**
     * @param iaBudgetId The iaBudgetId to set.
     */
    public void setIaBudgetId(Long iaBudgetId) {
        this.iaBudgetId = iaBudgetId;
    }

    /**
     * @return Returns the lagerId.
     */
    public Long getLagerId() {
        return this.lagerId;
    }

    /**
     * @param lagerId The lagerId to set.
     */
    public void setLagerId(Long lagerId) {
        this.lagerId = lagerId;
    }

    /**
     * @return Returns the createdAt.
     */
    public Date getCreatedAt() {
        return this.createdAt;
    }

    /**
     * @param createdAt The createdAt to set.
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return Returns the createdFrom.
     */
    public String getCreatedFrom() {
        return this.createdFrom;
    }

    /**
     * @param createdFrom The createdFrom to set.
     */
    public void setCreatedFrom(String createdFrom) {
        this.createdFrom = createdFrom;
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

}


