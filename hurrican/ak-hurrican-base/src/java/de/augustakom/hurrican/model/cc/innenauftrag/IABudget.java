/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.01.2007 13:26:54
 */
package de.augustakom.hurrican.model.cc.innenauftrag;

import java.util.*;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell fuer ein Innenauftrags-Budget. <br> Das Budget ist einem Innenauftrag zugeordnet und wird von einem
 * Projektleiter verwaltet. <br> Ist ein Budget geschlossen oder storniert koennen keine Erfassungen darauf mehr
 * vorgenommen werden (z.B. Materialentnahme zuordnet).
 *
 *
 */
public class IABudget extends AbstractCCIDModel {

    private static final long serialVersionUID = -7187754114528944680L;

    private Long iaId = null;
    public static final String PROJEKTLEITER = "projektleiter";
    private String projektleiter = null;
    public static final String BUDGET = "budget";
    private Float budget = null;
    public static final String CREATED_AT = "createdAt";
    private Date createdAt = null;
    public static final String CLOSED_AT = "closedAt";
    private Date closedAt = null;
    public static final String CANCELLED = "cancelled";
    private Boolean cancelled = null;
    public static final String BUDGET_USER_ID = "budgetUserId";
    private Long budgetUserId = null;
    public static final String RESPONSIBLE_USER_ID = "responsibleUserId";
    private Long responsibleUserId = null;
    public static final String PLANNED_FINISH_DATE = "plannedFinishDate";
    private Date plannedFinishDate = null;
    public static final String IA_LEVEL_1 = "iaLevel1";
    private String iaLevel1 = null;
    private String iaLevel1Id = null;
    public static final String IA_LEVEL_3 = "iaLevel3";
    private String iaLevel3 = null;
    private String iaLevel3Id = null;
    public static final String IA_LEVEL_5 = "iaLevel5";
    private String iaLevel5 = null;
    private String iaLevel5Id = null;

    /**
     * Ueberprueft, ob das Budget noch 'offen' ist. <br> Dies ist dann der Fall, wenn closedAt 'null' und
     * cancelled=false ist.
     *
     * @return
     *
     */
    public boolean isOpen() {
        return ((closedAt == null) && !BooleanTools.nullToFalse(cancelled)) ? true : false;
    }

    public Float getBudget() {
        return this.budget;
    }

    public void setBudget(Float budget) {
        this.budget = budget;
    }

    public Boolean getCancelled() {
        return this.cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Date getClosedAt() {
        return this.closedAt;
    }

    public void setClosedAt(Date closedAt) {
        this.closedAt = closedAt;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getProjektleiter() {
        return this.projektleiter;
    }

    public void setProjektleiter(String projektleiter) {
        this.projektleiter = projektleiter;
    }

    public Long getIaId() {
        return this.iaId;
    }

    public void setIaId(Long iaId) {
        this.iaId = iaId;
    }

    public Long getBudgetUserId() {
        return budgetUserId;
    }

    public void setBudgetUserId(Long budgetUserId) {
        this.budgetUserId = budgetUserId;
    }

    public Long getResponsibleUserId() {
        return responsibleUserId;
    }

    public void setResponsibleUserId(Long responsibleUserId) {
        this.responsibleUserId = responsibleUserId;
    }

    public Date getPlannedFinishDate() {
        return plannedFinishDate;
    }

    public void setPlannedFinishDate(Date plannedFinishDate) {
        this.plannedFinishDate = plannedFinishDate;
    }

    /**
     * @return SAP-Investitionsstrukur Ebene 1 in Hurrican
     */
    public String getIaLevel1() {
        return iaLevel1;
    }

    /**
     * @param iaLevel1 setzen der SAP-Investitionsstrukur Ebene 1 in Hurrican
     */
    public void setIaLevel1(String iaLevel1) {
        this.iaLevel1 = iaLevel1;
    }

    /**
     * @return SAP-Investitionsstrukur Ebene 3 in Hurrican
     */
    public String getIaLevel3() {
        return iaLevel3;
    }

    /**
     * @param iaLevel3 setzen der SAP-Investitionsstrukur Ebene 3 in Hurrican
     */
    public void setIaLevel3(String iaLevel3) {
        this.iaLevel3 = iaLevel3;
    }

    /**
     * @return SAP-Investitionsstrukur Ebene 5 in Hurrican
     */
    public String getIaLevel5() {
        return iaLevel5;
    }

    /**
     * @param iaLevel5 setzen der SAP-Investitionsstrukur Ebene 5 in Hurrican
     */
    public void setIaLevel5(String iaLevel5) {
        this.iaLevel5 = iaLevel5;
    }

    public String getIaLevel1Id() {
        return iaLevel1Id;
    }

    public void setIaLevel1Id(String iaLevel1Id) {
        this.iaLevel1Id = iaLevel1Id;
    }

    public String getIaLevel3Id() {
        return iaLevel3Id;
    }

    public void setIaLevel3Id(String iaLevel3Id) {
        this.iaLevel3Id = iaLevel3Id;
    }

    public String getIaLevel5Id() {
        return iaLevel5Id;
    }

    public void setIaLevel5Id(String iaLevel5Id) {
        this.iaLevel5Id = iaLevel5Id;
    }

}
