/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2007 15:31:11
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;


/**
 * Modell fuer die Abbildung eines Rangierungs-Auftrags. <br> Ueber dieses Modell wird eine neue Rangierung an einem HVT
 * veranlasst. Ueber die Datumsfelder ist ersichtlich, in welchem Status sich der Auftrag gerade befindet.
 *
 *
 */
public class RangierungsAuftrag extends AbstractCCIDModel {

    private Long hvtStandortId = null;
    private Integer anzahlPorts = null;
    private Long physiktypParent = null;
    private Long physiktypChild = null;
    private String auftragVon = null;
    private Date auftragAm = null;
    private Date faelligAm = null;
    private String definiertVon = null;
    private Date definiertAm = null;
    private String technikBearbeiter = null;
    private String ausgefuehrtVon = null;
    private Date ausgefuehrtAm = null;
    private Float technikStunden = null;
    private Boolean cancelled = null;
    private String cancelledFrom = null;

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
     * @return Returns the anzahlPorts.
     */
    public Integer getAnzahlPorts() {
        return anzahlPorts;
    }

    /**
     * @param anzahlPorts The anzahlPorts to set.
     */
    public void setAnzahlPorts(Integer anzahlPorts) {
        this.anzahlPorts = anzahlPorts;
    }

    /**
     * @return Returns the physiktypParent.
     */
    public Long getPhysiktypParent() {
        return physiktypParent;
    }

    /**
     * @param physiktypParent The physiktypParent to set.
     */
    public void setPhysiktypParent(Long physiktypParent) {
        this.physiktypParent = physiktypParent;
    }

    /**
     * @return Returns the physiktypChild.
     */
    public Long getPhysiktypChild() {
        return physiktypChild;
    }

    /**
     * @param physiktypChild The physiktypChild to set.
     */
    public void setPhysiktypChild(Long physiktypChild) {
        this.physiktypChild = physiktypChild;
    }

    /**
     * @return Returns the auftragVon.
     */
    public String getAuftragVon() {
        return auftragVon;
    }

    /**
     * @param auftragVon The auftragVon to set.
     */
    public void setAuftragVon(String auftragVon) {
        this.auftragVon = auftragVon;
    }

    /**
     * @return Returns the auftragAm.
     */
    public Date getAuftragAm() {
        return auftragAm;
    }

    /**
     * @param auftragAm The auftragAm to set.
     */
    public void setAuftragAm(Date auftragAm) {
        this.auftragAm = auftragAm;
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
     * @return Returns the definiertVon.
     */
    public String getDefiniertVon() {
        return definiertVon;
    }

    /**
     * @param definiertVon The definiertVon to set.
     */
    public void setDefiniertVon(String definiertVon) {
        this.definiertVon = definiertVon;
    }

    /**
     * @return Returns the definiertAm.
     */
    public Date getDefiniertAm() {
        return definiertAm;
    }

    /**
     * @param definiertAm The definiertAm to set.
     */
    public void setDefiniertAm(Date definiertAm) {
        this.definiertAm = definiertAm;
    }

    /**
     * @return Returns the ausgefuehrtVon.
     */
    public String getAusgefuehrtVon() {
        return ausgefuehrtVon;
    }

    /**
     * @param ausgefuehrtVon The ausgefuehrtVon to set.
     */
    public void setAusgefuehrtVon(String ausgefuehrtVon) {
        this.ausgefuehrtVon = ausgefuehrtVon;
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

    /**
     * @return Returns the cancelled.
     */
    public Boolean getCancelled() {
        return cancelled;
    }

    /**
     * @param cancelled The cancelled to set.
     */
    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * @return Returns the cancelledFrom.
     */
    public String getCancelledFrom() {
        return cancelledFrom;
    }

    /**
     * @param cancelledFrom The cancelledFrom to set.
     */
    public void setCancelledFrom(String cancelledFrom) {
        this.cancelledFrom = cancelledFrom;
    }

    /**
     * @return Returns the technikStunden.
     */
    public Float getTechnikStunden() {
        return technikStunden;
    }

    /**
     * @param technikStunden The technikStunden to set.
     */
    public void setTechnikStunden(Float technikStunden) {
        this.technikStunden = technikStunden;
    }

    /**
     * @return Returns the technikBearbeiter.
     */
    public String getTechnikBearbeiter() {
        return technikBearbeiter;
    }

    /**
     * @param technikBearbeiter The technikBearbeiter to set.
     */
    public void setTechnikBearbeiter(String technikBearbeiter) {
        this.technikBearbeiter = technikBearbeiter;
    }

}


