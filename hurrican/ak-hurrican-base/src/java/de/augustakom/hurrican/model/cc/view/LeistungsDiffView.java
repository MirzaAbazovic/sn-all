/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.07.2006 15:37:42
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * View-Modell, um Leistungsdifferenzen zwischen dem Billing- und CC-System zu ermitteln. <br>
 *
 *
 */
public class LeistungsDiffView extends AbstractCCModel implements CCAuftragModel, DebugModel {

    private Long auftragId = null;
    private Long techLeistungId = null;
    private String techLsName = null;
    private Long quantity = null;
    private Date aktivVon = null;
    private Date aktivBis = null;
    private boolean zugang = false;

    /**
     * Default-Const.
     */
    public LeistungsDiffView() {
        super();
    }

    /**
     * @param auftragId
     * @param auftragNoOrig
     * @param techLeistungId
     */
    public LeistungsDiffView(Long auftragId, Long techLeistungId) {
        super();
        this.auftragId = auftragId;
        this.techLeistungId = techLeistungId;
    }

    /**
     * @return Returns the aktivBis.
     */
    public Date getAktivBis() {
        return this.aktivBis;
    }

    /**
     * @param aktivBis The aktivBis to set.
     */
    public void setAktivBis(Date aktivBis) {
        this.aktivBis = aktivBis;
    }

    /**
     * @return Returns the aktivVon.
     */
    public Date getAktivVon() {
        return this.aktivVon;
    }

    /**
     * @param aktivVon The aktivVon to set.
     */
    public void setAktivVon(Date aktivVon) {
        this.aktivVon = aktivVon;
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.CCAuftragModel#getAuftragId()
     */
    @Override
    public Long getAuftragId() {
        return this.auftragId;
    }

    /**
     * @param auftragId The auftragId to set.
     */
    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the techLeistungId.
     */
    public Long getTechLeistungId() {
        return this.techLeistungId;
    }

    /**
     * @param techLeistungId The techLeistungId to set.
     */
    public void setTechLeistungId(Long techLeistungId) {
        this.techLeistungId = techLeistungId;
    }

    /**
     * @return Returns the zugang.
     */
    public boolean isZugang() {
        return this.zugang;
    }

    /**
     * @param zugang The zugang to set.
     */
    public void setZugang(boolean zugang) {
        this.zugang = zugang;
    }

    /**
     * Negation von <code>isZugang</code>
     */
    public boolean isKuendigung() {
        return !isZugang();
    }

    /**
     * @return Returns the techLsName.
     */
    public String getTechLsName() {
        return this.techLsName;
    }

    /**
     * @param techLsName The techLsName to set.
     */
    public void setTechLsName(String techLsName) {
        this.techLsName = techLsName;
    }

    /**
     * Gibt die Anzahl der anzulegenden bzw. zu kuendigenden Positionen fuer eine bestimmte Leistung an. <br> Hierbei
     * handelt es sich um eine Gesamt-Zahl. Dem Auftrag sind bei n*Positionen zugeordnet. Beispiel: Anzahl = 2 -->
     * Auftrag erhaelt zwei Positionen mit gleicher Leistungs-ID.
     *
     * @return Returns the quantity.
     */
    public Long getQuantity() {
        return this.quantity;
    }

    /**
     * @param quantity The quantity to set.
     */
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    @Override
    public void debugModel(Logger logger) {
        if (logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + this.getClass().getName());
            logger.debug("  Auftrag-ID: " + getAuftragId());
            logger.debug("  TechLs-ID : " + getTechLeistungId());
            logger.debug("  Quantity  : " + getQuantity());
        }
    }

}


