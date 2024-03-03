/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2004 16:20:55
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;


/**
 * View-Modell fuer die Abbildung der Datenbank-View 'AuftragText_View'.
 *
 *
 */
public class BAuftragText extends AbstractBillingModel {

    private Long atextNo = null;
    private Long auftragNo = null;
    private String textTyp = null;
    private String eintrag = null;
    private String userw = null;
    private Date datew = null;

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

    /**
     * @return atextNo
     */
    public Long getAtextNo() {
        return atextNo;
    }

    /**
     * @param atextNo Festzulegender atextNo
     */
    public void setAtextNo(Long atextNo) {
        this.atextNo = atextNo;
    }

    /**
     * @return Returns the auftragNo.
     */
    public Long getAuftragNo() {
        return auftragNo;
    }

    /**
     * @param auftragNo The auftragNo to set.
     */
    public void setAuftragNo(Long auftragNo) {
        this.auftragNo = auftragNo;
    }

    /**
     * @return Returns the eintrag.
     */
    public String getEintrag() {
        return eintrag;
    }

    /**
     * @param eintrag The eintrag to set.
     */
    public void setEintrag(String eintrag) {
        this.eintrag = eintrag;
    }

    /**
     * @return Returns the textTyp.
     */
    public String getTextTyp() {
        return textTyp;
    }

    /**
     * @param textTyp The textTyp to set.
     */
    public void setTextTyp(String textTyp) {
        this.textTyp = textTyp;
    }
}


