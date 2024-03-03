/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2008 09:49:04
 */
package de.augustakom.hurrican.model.billing.history;

import java.util.*;

import de.augustakom.hurrican.model.billing.AbstractBillingModel;

/**
 * Abstrakte Klasse fuer alle Change-Modelle.
 *
 *
 */
public class AbstractChangeModel extends AbstractBillingModel {

    private Long changeNo = null;
    private Date gueltigVon = null;
    private Date ungueltigVon = null;
    private String userw = null;
    private Date datew = null;

    /**
     * @return changeNo
     */
    public Long getChangeNo() {
        return changeNo;
    }

    /**
     * @param changeNo Festzulegender changeNo
     */
    public void setChangeNo(Long changeNo) {
        this.changeNo = changeNo;
    }

    /**
     * @return datew
     */
    public Date getDatew() {
        return datew;
    }

    /**
     * @param datew Festzulegender datew
     */
    public void setDatew(Date datew) {
        this.datew = datew;
    }

    /**
     * @return gueltigVon
     */
    public Date getGueltigVon() {
        return gueltigVon;
    }

    /**
     * @param gueltigVon Festzulegender gueltigVon
     */
    public void setGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
    }

    /**
     * @return ungueltigVon
     */
    public Date getUngueltigVon() {
        return ungueltigVon;
    }

    /**
     * @param ungueltigVon Festzulegender ungueltigVon
     */
    public void setUngueltigVon(Date ungueltigVon) {
        this.ungueltigVon = ungueltigVon;
    }

    /**
     * @return userw
     */
    public String getUserw() {
        return userw;
    }

    /**
     * @param userw Festzulegender userw
     */
    public void setUserw(String userw) {
        this.userw = userw;
    }

}
