/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.10.2007 10:45:31
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCModel;


/**
 * View-Klasse fuer die Abbilung von Rangierungs-Auftraegen und den dazugehoerigen Budgets.
 *
 *
 */
public class RangierungsAuftragBudgetView extends AbstractCCModel {

    private Long rangierungsAuftragId = null;
    private Date faelligAm = null;
    private Long hvtStandortId = null;
    private Long innenauftragsId = null;
    private String iaNummer = null;
    private Float budget = null;
    private Date budgetCreatedAt = null;
    private Date budgetClosedAt = null;
    private Date ausgefuehrtAm = null;

    /**
     * @return Returns the rangierungsAuftragId.
     */
    public Long getRangierungsAuftragId() {
        return rangierungsAuftragId;
    }

    /**
     * @param rangierungsAuftragId The rangierungsAuftragId to set.
     */
    public void setRangierungsAuftragId(Long rangierungsAuftragId) {
        this.rangierungsAuftragId = rangierungsAuftragId;
    }

    /**
     * @return Returns the faelligAm.
     */
    public Date getFaelligAm() {
        return faelligAm;
    }

    /**
     * @param faelligAm The faelligAm to set.
     */
    public void setFaelligAm(Date faelligAm) {
        this.faelligAm = faelligAm;
    }

    /**
     * @return Returns the hvtStandortId.
     */
    public Long getHvtStandortId() {
        return hvtStandortId;
    }

    /**
     * @param hvtStandortId The hvtStandortId to set.
     */
    public void setHvtStandortId(Long hvtStandortId) {
        this.hvtStandortId = hvtStandortId;
    }

    /**
     * @return Returns the innenauftragsId.
     */
    public Long getInnenauftragsId() {
        return innenauftragsId;
    }

    /**
     * @param innenauftragsId The innenauftragsId to set.
     */
    public void setInnenauftragsId(Long innenauftragsId) {
        this.innenauftragsId = innenauftragsId;
    }

    /**
     * @return Returns the iaNummer.
     */
    public String getIaNummer() {
        return iaNummer;
    }

    /**
     * @param iaNummer The iaNummer to set.
     */
    public void setIaNummer(String iaNummer) {
        this.iaNummer = iaNummer;
    }

    /**
     * @return Returns the budget.
     */
    public Float getBudget() {
        return budget;
    }

    /**
     * @param budget The budget to set.
     */
    public void setBudget(Float budget) {
        this.budget = budget;
    }

    /**
     * @return Returns the budgetCreatedAt.
     */
    public Date getBudgetCreatedAt() {
        return budgetCreatedAt;
    }

    /**
     * @param budgetCreatedAt The budgetCreatedAt to set.
     */
    public void setBudgetCreatedAt(Date budgetCreatedAt) {
        this.budgetCreatedAt = budgetCreatedAt;
    }

    /**
     * @return Returns the budgetClosedAt.
     */
    public Date getBudgetClosedAt() {
        return budgetClosedAt;
    }

    /**
     * @param budgetClosedAt The budgetClosedAt to set.
     */
    public void setBudgetClosedAt(Date budgetClosedAt) {
        this.budgetClosedAt = budgetClosedAt;
    }

    /**
     * @return Returns the ausgefuehrtAm.
     */
    public Date getAusgefuehrtAm() {
        return ausgefuehrtAm;
    }

    /**
     * @param ausgefuehrtAm The ausgefuehrtAm to set.
     */
    public void setAusgefuehrtAm(Date ausgefuehrtAm) {
        this.ausgefuehrtAm = ausgefuehrtAm;
    }

}


