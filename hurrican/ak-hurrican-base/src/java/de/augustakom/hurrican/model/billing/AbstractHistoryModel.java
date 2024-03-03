/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.06.2004 17:03:30
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;

import de.augustakom.common.model.HistoryModel;


/**
 * Abstrakte Klasse fuer alle Billing-Modelle, die History-Informationen bieten.
 *
 *
 */
public class AbstractHistoryModel extends AbstractBillingModel implements HistoryModel {

    private Date gueltigVon = null;
    private Date gueltigBis = null;
    private String histStatus = null;
    private Integer histCnt = null;
    private Boolean histLast = null;

    /**
     * @return Returns the gueltigBis.
     */
    public Date getGueltigBis() {
        return gueltigBis;
    }

    /**
     * @param gueltigBis The gueltigBis to set.
     */
    public void setGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
    }

    /**
     * @return Returns the gueltigVon.
     */
    public Date getGueltigVon() {
        return gueltigVon;
    }

    /**
     * @param gueltigVon The gueltigVon to set.
     */
    public void setGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
    }

    /**
     * @return Returns the histCnt.
     */
    public Integer getHistCnt() {
        return histCnt;
    }

    /**
     * @param histCnt The histCnt to set.
     */
    public void setHistCnt(Integer histCnt) {
        this.histCnt = histCnt;
    }

    /**
     * @return Returns the histLast.
     */
    public Boolean isHistLast() {
        return histLast;
    }

    /**
     * @param histLast The histLast to set.
     */
    public void setHistLast(Boolean histLast) {
        this.histLast = histLast;
    }

    /**
     * @return Returns the histStatus.
     */
    public String getHistStatus() {
        return histStatus;
    }

    /**
     * @param histStatus The histStatus to set.
     */
    public void setHistStatus(String histStatus) {
        this.histStatus = histStatus;
    }
}
